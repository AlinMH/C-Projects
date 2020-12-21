import numpy as np
from keras.models import model_from_json
from keras.preprocessing.image import ImageDataGenerator
from sklearn.metrics import accuracy_score
from pandas import read_csv

from random import randint


def compute_accuracies_for_test_set(given_set):
    probabilities = best_model.predict_generator(given_set, steps=50)
    predictions = np.where(probabilities > 0.5, 1, 0)
    label_map = given_set.class_indices
    label_map = dict((v, k) for k, v in label_map.items())
    predictions = [label_map[k[0]] for k in predictions]
    i = 0

    patient_to_study_dict = {}
    for path in given_set.filepaths:
        patient_idx = path.find('patient')
        study_idx = path.find('study')
        img_idx = path.find('image')

        patient = path[patient_idx:study_idx - 1]
        study = path[study_idx: img_idx - 1]

        if predictions[i] == '1':
            value = 1
        else:
            value = -1

        patient_to_study_dict[patient] = patient_to_study_dict.get(patient, {})
        patient_to_study_dict[patient][study] = patient_to_study_dict[patient].get(study, 0) + value
        i += 1
    studies_counter = 0
    hits = 0
    for patient, study_dict in patient_to_study_dict.items():
        for study, val in study_dict.items():
            if val == 0:
                hits += randint(0, 1)
            if 'positive' in study and val > 0:
                hits += 1
            elif 'negative' in study and val < 0:
                hits += 1
            studies_counter += 1

    accuracy = hits / studies_counter
    predictions = list(map(lambda x: int(x), predictions))
    print("Test accuracy per study", accuracy)
    print("Test accuracy per image", accuracy_score(given_set.classes, predictions))


def compute_accuracies_for_train_set(given_set):
    probabilities = best_model.predict_generator(given_set, steps=576)
    predictions = np.where(probabilities > 0.5, 1, 0)
    label_map = given_set.class_indices
    label_map = dict((v, k) for k, v in label_map.items())
    predictions = [label_map[k[0]] for k in predictions]
    i = 0

    patient_to_study_dict = {}
    for path in given_set.filepaths:
        patient_idx = path.find('patient')
        study_idx = path.find('study')
        img_idx = path.find('image')

        patient = path[patient_idx:study_idx - 1]
        study = path[study_idx: img_idx - 1]

        if predictions[i] == '1':
            value = 1
        else:
            value = -1

        patient_to_study_dict[patient] = patient_to_study_dict.get(patient, {})
        patient_to_study_dict[patient][study] = patient_to_study_dict[patient].get(study, 0) + value
        i += 1
    studies_counter = 0
    hits = 0
    for patient, study_dict in patient_to_study_dict.items():
        for study, val in study_dict.items():
            if val == 0:
                hits += randint(0, 1)
            if 'positive' in study and val > 0:
                hits += 1
            elif 'negative' in study and val < 0:
                hits += 1
            studies_counter += 1

    accuracy = hits / studies_counter
    predictions = list(map(lambda x: int(x), predictions))
    print("Train accuracy per study", accuracy)
    print("Train accuracy per image", accuracy_score(given_set.classes, predictions))


if __name__ == '__main__':
    df_train = read_csv('MURA-v1.1/train_image_paths.csv')
    df_test = read_csv('MURA-v1.1/valid_image_paths.csv')
    df_train['label'] = ['1' if 'positive' in x else '0' for x in df_train['path']]
    df_test['label'] = ['1' if 'positive' in x else '0' for x in df_test['path']]

    train_data_gen = ImageDataGenerator()
    test_data_gen = ImageDataGenerator()

    train_set = train_data_gen.flow_from_dataframe(df_train, x_col="path", y_col="label", color_mode="grayscale",
                                                   class_mode="binary", target_size=(244, 244), batch_size=64,
                                                   shuffle=False)

    test_set = test_data_gen.flow_from_dataframe(df_test, x_col="path", y_col="label", color_mode="grayscale",
                                                 class_mode="binary", target_size=(244, 244), batch_size=64,
                                                 shuffle=False)

    model_architecture = open('model/feature_maps_v2.json', 'r')
    best_model = model_from_json(model_architecture.read())
    model_architecture.close()
    # Load best model's weights
    best_model.load_weights('model/weights/feature_maps_v2_cnn_weights_20ep.hdf5')
    # Compile the best model
    best_model.compile(optimizer='sgd', loss='binary_crossentropy', metrics=['accuracy'])
    # Evaluate on test data

    compute_accuracies_for_test_set(test_set)
    compute_accuracies_for_train_set(train_set)
