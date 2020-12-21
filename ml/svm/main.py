import numpy as np
import matplotlib.pyplot as plt

from svmutil import *
from argparse import ArgumentParser
from sklearn.metrics import confusion_matrix

TRAIN_PERCENTAGE = 0.7


def plot_confusion_matrix(y_true, y_pred, classes, title,
                          cmap=plt.cm.Blues):
    cm = confusion_matrix(y_true, y_pred)
    fig, ax = plt.subplots()
    im = ax.imshow(cm, interpolation='nearest', cmap=cmap)
    ax.figure.colorbar(im, ax=ax)
    ax.set(xticks=np.arange(cm.shape[1]),
           yticks=np.arange(cm.shape[0]),
           xticklabels=classes, yticklabels=classes,
           title=None,
           ylabel='True label',
           xlabel='Predicted label')

    plt.setp(ax.get_xticklabels(), rotation=45, ha="right",
             rotation_mode="anchor")
    fmt = 'd'
    thresh = cm.max() / 2.
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            ax.text(j, i, format(cm[i, j], fmt),
                    ha="center", va="center",
                    color="white" if cm[i, j] > thresh else "black")
    fig.tight_layout()
    fig.savefig(title + ".png")
    return ax


def classify_skin_nonskin(kernel_type, degree, gamma, C, coef0):
    y, x = svm_read_problem('processed_data/shuffled_skin_nonskin')

    # training data
    split_idx = int(TRAIN_PERCENTAGE * len(y))
    y_train = y[:split_idx]
    x_train = x[:split_idx]

    # test data
    y_test = y[split_idx:]
    x_test = x[split_idx:]

    problem = svm_problem(y_train, x_train)
    param = svm_parameter()
    param.kernel_type = kernel_type
    param.degree = degree
    param.gamma = gamma
    param.C = C
    param.coef0 = coef0

    base_title = str.format("skin_nonskin_kernel{}_C{}_gamma{}_coef0{}_degree{}", kernel_type, C, gamma, coef0, degree)
    m = svm_train(problem, param)

    pred_labels, p_acc, _ = svm_predict(y_test, x_test, m)
    plot_confusion_matrix(y_test, pred_labels, ['1', '2'], title=base_title + "_test")
    (ACC, MSE, _) = p_acc
    print("ACC:", ACC)
    print("MSE:", MSE)

    pred_labels, _, _ = svm_predict(y_train, x_train, m)
    plot_confusion_matrix(y_train, pred_labels, ['1', '2'], title=base_title + "_train")


def classify_news20(method, kernel_type, degree, gamma, C, coef0):
    y_train, x_train = svm_read_problem('news20')
    y_test, x_test = svm_read_problem('news20.t')

    nr_of_classes = 20
    test_probabilities = []
    train_probabilities = []

    y_pred_train = np.zeros(len(y_train))
    y_pred_test = np.zeros(len(y_test))
    if method == "ova":
        for c in range(1, nr_of_classes + 1):
            train_labels = [0 if y != c else c for y in y_train]
            problem = svm_problem(train_labels, x_train)
            param = svm_parameter("-b 1")
            param.kernel_type = kernel_type
            param.degree = degree
            param.gamma = gamma
            param.C = C
            param.coef0 = coef0
            model = svm_train(problem, param)
            labels, _, pvals = svm_predict(y_test, x_test, model, "-q -b 1")
            insert_probabilities(labels, pvals, test_probabilities, y_test)

            labels, _, pvals = svm_predict(y_train, x_train, model, "-q -b 1")
            insert_probabilities(labels, pvals, train_probabilities, y_train)

        ova_predict(nr_of_classes, test_probabilities, y_pred_test, y_test)
        ova_predict(nr_of_classes, train_probabilities, y_pred_train, y_train)

    elif method == "ovo":
        train_pred_labels = np.zeros((nr_of_classes, nr_of_classes, len(y_train)))
        test_pred_labels = np.zeros((nr_of_classes, nr_of_classes, len(y_test)))
        for i in range(1, nr_of_classes + 1):
            for j in range(i + 1, nr_of_classes + 1):
                train_labels = []
                train_data = []
                for data_idx in range(len(y_train)):
                    label = y_train[data_idx]
                    if label == i or label == j:
                        train_labels.append(label)
                        train_data.append(x_train[data_idx])
                problem = svm_problem(train_labels, train_data)
                param = svm_parameter()
                param.kernel_type = kernel_type
                param.degree = degree
                param.gamma = gamma
                param.C = C
                param.coef0 = coef0
                model = svm_train(problem, param)

                test_pred_labels[i - 1, j - 1], _, _ = svm_predict(y_test, x_test, model, "-q")
                train_pred_labels[i - 1, j - 1], _, _ = svm_predict(y_train, x_train, model, "-q")

        y_pred_test = np.zeros(len(y_test))

        for data_idx in range(len(y_test)):
            y_pred_point = []
            for i in range(1, nr_of_classes + 1):
                for j in range(i + 1, nr_of_classes + 1):
                    vote = test_pred_labels[i - 1, j - 1, data_idx]
                    y_pred_point.append(vote)
            y_pred_test[data_idx] = max(set(y_pred_point), key=y_pred_point.count)

        for data_idx in range(len(y_train)):
            y_pred_point = []
            for i in range(1, nr_of_classes + 1):
                for j in range(i + 1, nr_of_classes + 1):
                    vote = train_pred_labels[i - 1, j - 1, data_idx]
                    y_pred_point.append(vote)
            y_pred_train[data_idx] = max(set(y_pred_point), key=y_pred_point.count)

    classes = [str(i) for i in range(1, nr_of_classes + 1)]
    title = str.format("news20_method{}_kernel{}_C{}_gamma{}_coef0{}_degree{}", method, kernel_type, C, gamma, coef0,
                       degree)
    plot_confusion_matrix(y_test, y_pred_test, classes, title=title + "_test")
    plot_confusion_matrix(y_train, y_pred_train, classes, title=title + "_train")
    acc, mse, _ = evaluations(y_test, y_pred_test)
    print("ACC:", acc)
    print("MSE:", mse)


def ova_predict(nr_of_classes, test_probabilities, y_pred, y_test):
    for i in range(len(y_test)):
        max_prob = 0.0
        predicted_class = 0.0
        for c in range(1, nr_of_classes + 1):
            prob = test_probabilities[c - 1][i]
            if max_prob < prob:
                max_prob = prob
                predicted_class = c
        y_pred[i] = predicted_class


def insert_probabilities(labels, pvals, probabilities, y):
    local_probs = []
    for i in range(len(y)):
        if labels[i] != 0:
            prob = max(pvals[i])
        else:
            prob = min(pvals[i])
        local_probs.append(prob)
    probabilities.append(local_probs)


if __name__ == '__main__':
    parser = ArgumentParser()

    parser.add_argument("--data_file", type=str, default="skin_nonskin")
    parser.add_argument("--method", type=str, default="ova")
    parser.add_argument("--kernel_type", type=int, default=0, help="Kernel type")
    parser.add_argument("--C", type=int, default=1, help="C")
    parser.add_argument("--degree", type=int, default=3, help="Degree")
    parser.add_argument("--gamma", type=float, default=0.0, help="Gamma")
    parser.add_argument("--coef0", type=float, default=0.0, help="Coef0")

    args = parser.parse_args()

    if args.data_file == "skin_nonskin":
        classify_skin_nonskin(kernel_type=args.kernel_type, degree=args.degree,
                              gamma=args.gamma, C=args.C, coef0=args.coef0)
    else:
        classify_news20(method=args.method, kernel_type=args.kernel_type, degree=args.degree,
                        gamma=args.gamma, C=args.C, coef0=args.coef0)
