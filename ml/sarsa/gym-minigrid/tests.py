from sarsa_skel import *


def plot_egreedy(map):
    c1 = 0.5
    lr1 = 0.1
    d1 = 0.99
    q01 = 0
    steps1, avg_lengths1, avg_returns1 = sarsa_egreedy(map_file=map, learning_rate=lr1,
                                                       discount=d1, const=c1, train_episodes=500, q0=q01,
                                                       final_show=False)

    c2 = 0.5
    lr2 = 0.1
    d2 = 0.99
    q02 = 0.2
    steps2, avg_lengths2, avg_returns2 = sarsa_egreedy(map_file=map, learning_rate=lr2,
                                                       discount=d2, const=c2, train_episodes=500, q0=q02,
                                                       final_show=False)
    c3 = 0.5
    lr3 = 0.1
    d3 = 0.99
    q03 = 0.5
    steps3, avg_lengths3, avg_returns3 = sarsa_egreedy(map_file=map, learning_rate=lr3,
                                                       discount=d3, const=c3, train_episodes=500, q0=q03,
                                                       final_show=False)
    c4 = 0.5
    lr4 = 0.1
    d4 = 0.99
    q04 = 1
    steps4, avg_lengths4, avg_returns4 = sarsa_egreedy(map_file=map, learning_rate=lr4,
                                                       discount=d4, const=c4, train_episodes=500, q0=q04,
                                                       final_show=False)
    _fig, (ax1, ax2) = plt.subplots(ncols=2)
    ax1.plot(steps1, avg_lengths1, label="egreedy c:" + str(c1) + " lr=" + str(lr1) + " q0=" + str(q01))
    ax1.plot(steps2, avg_lengths2, label="egreedy c:" + str(c2) + " lr=" + str(lr2) + " q0=" + str(q02))
    ax1.plot(steps3, avg_lengths3, label="egreedy c:" + str(c3) + " lr=" + str(lr3) + " q0=" + str(q03))
    ax1.plot(steps4, avg_lengths4, label="egreedy c:" + str(c4) + " lr=" + str(lr4) + " q0=" + str(q04))
    ax1.set_title("Average episode length")
    ax1.legend()

    ax2.plot(steps1, avg_returns1, label="egreedy c:" + str(c1) + " lr=" + str(lr1) + " q0=" + str(q01))
    ax2.plot(steps2, avg_returns2, label="egreedy c:" + str(c2) + " lr=" + str(lr2) + " q0=" + str(q02))
    ax2.plot(steps3, avg_returns3, label="egreedy c:" + str(c3) + " lr=" + str(lr3) + " q0=" + str(q03))
    ax2.plot(steps4, avg_returns4, label="egreedy c:" + str(c4) + " lr=" + str(lr4) + " q0=" + str(q04))
    ax2.set_title("Average episode return")
    ax2.legend()
    plt.show()


def plot_softmax(map):
    lr1 = 0.1
    d1 = 0.99
    steps1, avg_lengths1, avg_returns1 = sarsa_softmax(map_file=map, learning_rate=lr1,
                                                       discount=d1, train_episodes=500, q0=0,
                                                       final_show=False)

    lr2 = 0.2
    d2 = 0.99
    steps2, avg_lengths2, avg_returns2 = sarsa_softmax(map_file=map, learning_rate=lr2,
                                                       discount=d2, train_episodes=500, q0=0,
                                                       final_show=False)
    lr3 = 0.4
    d3 = 0.99
    steps3, avg_lengths3, avg_returns3 = sarsa_softmax(map_file=map, learning_rate=lr3,
                                                       discount=d3, train_episodes=500, q0=0,
                                                       final_show=False)
    lr4 = 0.8
    d4 = 0.99
    steps4, avg_lengths4, avg_returns4 = sarsa_softmax(map_file=map, learning_rate=lr4,
                                                       discount=d4, train_episodes=500, q0=0,
                                                       final_show=False)
    _fig, (ax1, ax2) = plt.subplots(ncols=2)
    ax1.plot(steps1, avg_lengths1, label="softmax lr=" + str(lr1))
    ax1.plot(steps2, avg_lengths2, label="softmax lr=" + str(lr2))
    ax1.plot(steps3, avg_lengths3, label="softmax lr=" + str(lr3))
    ax1.plot(steps4, avg_lengths4, label="softmax lr=" + str(lr4))
    ax1.set_title("Average episode length")
    ax1.legend()

    ax2.plot(steps1, avg_returns1, label="softmax lr=" + str(lr1))
    ax2.plot(steps2, avg_returns2, label="softmax lr=" + str(lr2))
    ax2.plot(steps3, avg_returns3, label="softmax lr=" + str(lr3))
    ax2.plot(steps4, avg_returns4, label="softmax lr=" + str(lr4))
    ax2.set_title("Average episode return")
    ax2.legend()
    plt.show()


if __name__ == '__main__':
    plot_softmax("MiniGrid-Empty-6x6-v0")
