from keras.preprocessing.image import ImageDataGenerator
from keras.models import Sequential
from keras.layers.convolutional import Conv2D
from keras.layers import MaxPooling2D, Dense, Dropout, Flatten
from keras.callbacks import ModelCheckpoint, TensorBoard
from pandas import read_csv
from matplotlib import pyplot

from time import time


def plot_history():
    pyplot.plot(history.history['loss'], label='train')
    pyplot.plot(history.history['val_loss'], label='test')
    pyplot.legend()
    pyplot.show()


if __name__ == '__main__':
    # data prep
    df_train = read_csv('MURA-v1.1/train_image_paths.csv')
    df_test = read_csv('MURA-v1.1/valid_image_paths.csv')
    df_train['label'] = ['1' if 'positive' in x else '0' for x in df_train['path']]
    df_test['label'] = ['1' if 'positive' in x else '0' for x in df_test['path']]

    train_data_gen = ImageDataGenerator()
    test_data_gen = ImageDataGenerator()

    training_set = train_data_gen.flow_from_dataframe(df_train, x_col="path", y_col="label", color_mode="grayscale",
                                                      class_mode="binary", target_size=(244, 244), batch_size=64)
    test_set = test_data_gen.flow_from_dataframe(df_test, x_col="path", y_col="label", color_mode="grayscale",
                                                 class_mode="binary", target_size=(244, 244), batch_size=64)

    # design the network
    model = Sequential()
    model.add(Conv2D(filters=128, kernel_size=(7, 7), strides=2, activation='relu', input_shape=(244, 244, 1)))
    model.add(MaxPooling2D((2, 2), 2))
    model.add(Conv2D(filters=256, kernel_size=(5, 5), strides=2, activation='relu'))
    # model.add(Conv2D(filters=128, kernel_size=(5, 5), strides=1, activation='relu'))  # added
    model.add(MaxPooling2D((2, 2), 2))
    # model.add(Conv2D(filters=256, kernel_size=(3, 3), strides=1, activation='relu'))  # added
    model.add(Conv2D(filters=384, kernel_size=(3, 3), strides=1, activation='relu'))
    model.add(Conv2D(filters=512, kernel_size=(3, 3), strides=1, activation='relu'))
    model.add(Conv2D(filters=384, kernel_size=(3, 3), strides=1, activation='relu'))
    model.add(Conv2D(filters=384, kernel_size=(3, 3), strides=1, activation='relu'))
    model.add(MaxPooling2D((2, 2), 2))
    model.add(Dropout(0.5))
    model.add(Dense(2048, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Flatten())
    model.add(Dense(1, activation='sigmoid'))

    model.compile(optimizer='sgd', loss='binary_crossentropy', metrics=['accuracy'])

    with open("model/feature_maps_v2.json", "w") as m:
        m.write(model.to_json())

    tensorboard = TensorBoard(log_dir='log/{}'.format(time()))
    checkpointer = ModelCheckpoint(filepath='model/weights/feature_maps_v2_cnn_weights_20ep.hdf5'
                                   , verbose=0
                                   , save_best_only=True)

    history = model.fit_generator(training_set,
                                  epochs=20,
                                  verbose=2,
                                  steps_per_epoch=576,
                                  validation_data=test_set,
                                  validation_steps=50,
                                  callbacks=[checkpointer, tensorboard])
    plot_history()
