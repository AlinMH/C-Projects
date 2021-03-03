import matplotlib.pyplot as plt
import numpy as np
import gym
import time
import math
import gym_minigrid

from argparse import ArgumentParser
from random import choice


def e_greedy(Q, s, actions, q0, N, const):
    values = np.zeros(len(actions) + 1)
    for a in actions:
        values[a] = Q.get((s, a), q0)

    if np.all(values == values[0]):
        return choice(actions)

    epsilon = const / N.get(s, 1)
    p2 = epsilon / len(actions)
    p1 = p2 + (1 - epsilon)

    probs = np.array([p1, p2])
    probs /= probs.sum()
    return np.random.choice([np.argmax(values), choice(actions)], p=probs)


def beta(ns, actions_to_value, actions):
    log = math.log2(ns)

    q_max = -math.inf
    for a1 in actions:
        for a2 in actions:
            if a1 != a2:
                val = abs(actions_to_value[a1] - actions_to_value[a2])
                if q_max < val:
                    q_max = val

    return log / q_max


def softmax(Q, s, actions, q0, N):
    actions_to_value = {}
    for a in actions:
        actions_to_value[a] = Q.get((s, a), q0)

    ok = True
    first = actions_to_value[0]
    for value in actions_to_value.values():
        if first != value:
            ok = False
            break

    if ok:
        return choice(actions)

    b = beta(N[s], actions_to_value, actions)
    exps = [np.exp(b * Q.get((s, a), q0)) for a in actions]
    sum_of_exps = sum(exps)
    softmax_probs = [exp / sum_of_exps for exp in exps]
    return np.random.choice(a=actions, p=softmax_probs)


def sarsa_softmax(map_file, learning_rate, discount, train_episodes, q0, final_show):
    report_freq = 100

    env = gym.make(map_file)

    steps, avg_returns, avg_lengths = [], [], []
    recent_returns, recent_lengths = [], []
    crt_return, crt_length = 0, 0

    N = {}
    Q = {}
    done = False
    obs = env.reset()

    actions = [a for a in env.actions if a != env.actions.done and a != env.actions.drop]

    for current_episode in range(1, train_episodes + 1):
        s = (str(obs['image']), obs['direction'])
        action = softmax(Q, s, actions, q0, N)

        while not done:
            N[s] = N.get(s, 1) + 1
            obs, reward, done, _ = env.step(action)
            next_s = (str(obs['image']), obs['direction'])
            next_action = softmax(Q, next_s, actions, q0, N)
            qsa = Q.get((s, action), q0)
            Q[(s, action)] = qsa + learning_rate * (reward + discount * Q.get((next_s, next_action), q0) - qsa)

            action = next_action
            s = next_s

            crt_return += reward
            crt_length += 1

        obs = env.reset()

        done = False
        recent_returns.append(crt_return)
        recent_lengths.append(crt_length)
        crt_return, crt_length = 0, 0

        if current_episode % report_freq == 0:
            avg_return = np.mean(recent_returns)
            avg_length = np.mean(recent_lengths)
            steps.append(current_episode)
            avg_returns.append(avg_return)
            avg_lengths.append(avg_length)

            print(  # pylint: disable=bad-continuation
                f"Step {current_episode:4d}"
                f" | Avg. return = {avg_return:.2f}"
                f" | Avg. ep. length: {avg_length:.2f}"
            )
            recent_returns.clear()
            recent_lengths.clear()

    if final_show:
        renderer = env.render('human')
        s = (str(obs['image']), obs['direction'])
        action = softmax(Q, s, actions, q0, N)
        while not done:
            env.render('human')
            time.sleep(0.5)

            if renderer.window is None:
                break

            N[s] = N.get(s, 1) + 1
            obs, reward, done, _ = env.step(action)
            next_s = (str(obs['image']), obs['direction'])
            next_action = softmax(Q, next_s, actions, q0, N)
            qsa = Q.get((s, action), q0)
            Q[(s, action)] = qsa + learning_rate * (reward + discount * Q.get((next_s, next_action), q0) - qsa)
            action = next_action
            s = next_s
    return steps, avg_lengths, avg_returns


def sarsa_egreedy(map_file, learning_rate, discount, const, train_episodes, q0, final_show):
    report_freq = 100

    env = gym.make(map_file)

    steps, avg_returns, avg_lengths = [], [], []
    recent_returns, recent_lengths = [], []
    crt_return, crt_length = 0, 0

    N = {}
    Q = {}
    done = False
    obs = env.reset()

    actions = [a for a in env.actions if a != env.actions.done and a != env.actions.drop]

    for current_episode in range(1, train_episodes + 1):
        s = (str(obs['image']), obs['direction'])
        action = e_greedy(Q, s, actions, q0, N, const)

        while not done:
            N[s] = N.get(s, 1) + 1
            obs, reward, done, _ = env.step(action)
            next_s = (str(obs['image']), obs['direction'])
            next_action = e_greedy(Q, next_s, actions, q0, N, const)
            qsa = Q.get((s, action), q0)
            Q[(s, action)] = qsa + learning_rate * (reward + discount * Q.get((next_s, next_action), q0) - qsa)

            action = next_action
            s = next_s

            crt_return += reward
            crt_length += 1

        obs = env.reset()
        done = False
        recent_returns.append(crt_return)
        recent_lengths.append(crt_length)
        crt_return, crt_length = 0, 0

        if current_episode % report_freq == 0:
            avg_return = np.mean(recent_returns)
            avg_length = np.mean(recent_lengths)
            steps.append(current_episode)
            avg_returns.append(avg_return)
            avg_lengths.append(avg_length)

            print(  # pylint: disable=bad-continuation
                f"Step {current_episode:4d}"
                f" | Avg. return = {avg_return:.2f}"
                f" | Avg. ep. length: {avg_length:.2f}"
            )
            recent_returns.clear()
            recent_lengths.clear()

    if final_show:
        renderer = env.render('human')
        s = (str(obs['image']), obs['direction'])
        action = e_greedy(Q, s, actions, q0, N, const)
        while not done:
            env.render('human')
            time.sleep(0.5)

            if renderer.window is None:
                break

            N[s] = N.get(s, 1) + 1
            obs, reward, done, _ = env.step(action)
            next_s = (str(obs['image']), obs['direction'])
            next_action = e_greedy(Q, next_s, actions, q0, N, const)
            qsa = Q.get((s, action), q0)
            Q[(s, action)] = qsa + learning_rate * (reward + discount * Q.get((next_s, next_action), q0) - qsa)
            action = next_action
            s = next_s
    return steps, avg_lengths, avg_returns


if __name__ == "__main__":
    parser = ArgumentParser()

    parser.add_argument("--map_file", type=str, default="MiniGrid-Empty-6x6-v0",
                        help="File to read map from.")
    parser.add_argument("--method", type=str, default="egreedy",
                        help="Method used in sarsa.")

    # Meta-parameters
    parser.add_argument("--learning_rate", type=float, default=0.1,
                        help="Learning rate")
    parser.add_argument("--discount", type=float, default=0.99,
                        help="Value for the discount factor")
    parser.add_argument("--const", type=float, default=1,
                        help="Probability to choose a random action.")

    # Training and evaluation episodes
    parser.add_argument("--train_episodes", type=int, default=500,
                        help="Number of episodes")
    parser.add_argument("--q0", type=float, default=0.0,
                        help="q0 value")
    parser.add_argument("--final_show", dest="final_show",
                        action="store_true",
                        help="Demonstrate final strategy.")

    args = parser.parse_args()

    if args.method == "egreedy":
        steps, avg_lengths, avg_returns = sarsa_egreedy(args.map_file, args.learning_rate, args.discount, args.const,
                                                        args.train_episodes, args.q0, args.final_show)
    else:
        steps, avg_lengths, avg_returns = sarsa_softmax(args.map_file, args.learning_rate, args.discount,
                                                        args.train_episodes, args.q0, args.final_show)

    _fig, (ax1, ax2) = plt.subplots(ncols=2)

    ax1.plot(steps, avg_lengths, label=args.method)
    ax1.set_title("Average episode length")
    ax1.legend()

    ax2.plot(steps, avg_returns, label=args.method)
    ax2.set_title("Average episode return")
    ax2.legend()
    plt.show()
