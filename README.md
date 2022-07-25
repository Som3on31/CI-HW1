# CI-HW1
 For Neural Network Homework

 This project is aimed to demonstrate how a neural network works. The dataset used for this project is flood data set which contains 8 inputs and one desired output. We break down into 2 important classes

## ***Neuron.java***
 This class is to imitate how a neuron behaves. It uses ReLU (Rectified Linear activation Unit) as its activation function. This decistion is done as the vanishing gradient problem from using sigmoid function is fairly severe and thus increasing cost of training.

## ***NeuralNetwork.java***
 This class creates a network of connected neurons consisting of 3 main layers: Input, Hidden and Output
