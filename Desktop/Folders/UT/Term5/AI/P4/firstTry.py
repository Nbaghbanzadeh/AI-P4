from sklearn import tree
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import pandas as pd
import numpy as np
import random

def makeDecisionTree(features, droped):
    df = pd.read_csv('data.csv')
    trainIndex = int(len(df['age'])*0.8)
    testIndex = int(len(df['age'])*0.8)+1
    Y = df.iloc[0:trainIndex, [13]]
    X = df.iloc[0:trainIndex, features]
    clf = tree.DecisionTreeClassifier()
    clf = clf.fit(X, Y)
    predicted = []
    for i in range(testIndex, len(age)):
        predict = clf.predict([df.drop(['target', droped], axis=1).loc[i]])
        if(predict>0.5):
            predicted.append(1)
        else:
            predicted.append(0)
    accuracy = accuracy_score(df.iloc[testIndex:len(age), [13]], predicted)
    #print(accuracy)

df = pd.read_csv('data.csv')
trainIndex = int(len(df['age'])*0.8)
testIndex = int(len(df['age'])*0.8)+1
age = df['age']
sex = df['sex']
cp = df['cp']
trestbps = df['trestbps']
chol = df['chol']
fbs = df['fbs']
restecg = df['restecg']
thalach = df['thalach']
exang = df['exang']
oldpeak = df['oldpeak']
slope = df['slope']
ca = df['ca']
thal = df['thal']
target = df['target']
########################################################### 1
Y = df.iloc[0:trainIndex, [13]]
X = df.iloc[0:trainIndex, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]]
clf = tree.DecisionTreeClassifier()
clf = clf.fit(X, Y)
#########################   TEST ##########################
right = 0
wrong = 0
predicted = []
for i in range(testIndex, len(age)):
    predict = clf.predict([df.drop(['target'], axis=1).loc[i]])
    if(predict>0.5):
        predicted.append(1)
    else:
        predicted.append(0)
accuracy = accuracy_score(df.iloc[testIndex:len(age), [13]], predicted)
#print("Decision Tree", accuracy)
########################################################### 2.1 and 2.2
#   5 groups of 150
groups = []
for i in range(0, 5):
    group = []
    for i in range(0, 150):
        index = random.randint(0, trainIndex)
        group.append(df.loc[index])
    groups.append(group)
#   Training each group
groupsCLF = []
for i in range(0, 5):
    df1 = pd.DataFrame(groups[i])
    X1 = df1.iloc[:, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]]
    Y1 = df1.iloc[:, [13]]
    c = tree.DecisionTreeClassifier()
    c = c.fit(X1, Y1)
    groupsCLF.append(c)
#########################   TEST ##########################
predicted = []
for i in range(testIndex, len(age)):
    predicts = []
    for j in range(0, 5):
        predict = groupsCLF[j].predict([df.drop(['target'], axis=1).loc[i]])
        predicts.append(predict)
    predicts = np.array(predicts)
    if(np.mean(predicts)>0.5):
        predicted.append(1)
    else:
        predicted.append(0)
accuracy = accuracy_score(df.iloc[testIndex:len(age), [13]], predicted)
#print("Bagging", accuracy)
########################################################### 2.3
test = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
for k in range(0, 13):
    features = []
    for j in range(0, 13):
        if(j != k):
            features.append(j)
    makeDecisionTree(features, test[k])
    features = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
############################################################### 2.4
featuresIndex = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
featuresName = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
toChoose = []
n = 0
while(n < 5):
    index = int(random.randint(0, 12))
    if(not (index in toChoose)):
        toChoose.append(index)
        n += 1
toDrop = []
n = 0
for i in range(0, 13):
    if(not(i in toChoose)):
        toDrop.append(featuresName[i])
X = df.iloc[:, toChoose]
Y = df.iloc[:, [13]]
clf = tree.DecisionTreeClassifier()
clf = clf.fit(X, Y)
#########################   TEST ##########################
predicted = []
for i in range(testIndex, len(age)):
    predict = clf.predict([(df.drop(['target'], axis=1).drop(toDrop, axis=1)).loc[i]])
    if(predict>0.5):
        predicted.append(1)
    else:
        predicted.append(0)
accuracy = accuracy_score(df.iloc[testIndex:len(age), [13]], predicted)
#print("Randomly choose five", accuracy)
########################################################### 2.5
#make 500 decision trees with some features
featuresIndex = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
featuresName = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
decisionTrees = []
toDrops = []
toChooses = []
for x in range(0, 500):
    while(True):
        toChoose = []
        n = 0
        while(n < 5):
            index = int(random.randint(0, 12))
            if(not (index in toChoose)):
                toChoose.append(index)
                n += 1
        break
    toDrop = []
    for i in range(0, 13):
        if(not(i in toChoose)):
            toDrop.append(featuresName[i])
    X = df.iloc[:, toChoose]
    Y = df.iloc[:, [13]]
    clf = tree.DecisionTreeClassifier()
    clf = clf.fit(X, Y)
    decisionTrees.append(clf)
    toDrops.append(toDrop)
    toDrop.append('target')
#########################   TEST ##########################
predicted = []
for i in range(testIndex, len(age)):
    predicts = []
    for j in range(0, len(decisionTrees)):
        predict = decisionTrees[j].predict([(df.drop(toDrops[j], axis=1)).loc[i]])
        predicts.append(predict)
    predicts = np.array(predicts)
    if(np.mean(predicts)>0.5):
        predicted.append(1)
    else:
        predicted.append(0)
accuracy = accuracy_score(df.iloc[testIndex:len(age), [13]], predicted)
#print("Forest", accuracy)
###########################################################
