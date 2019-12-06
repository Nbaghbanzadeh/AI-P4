from sklearn import tree
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
import random

df = pd.read_csv('data.csv')
testDF = pd.read_csv('test.csv')
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
# X = [age, sex, cp, trestbps, chol, fbs, restecg, thalach, exang, oldpeak, slope, ca, thal]
# Y = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
Y = df.iloc[0:trainIndex, [13]]
X = df.iloc[0:trainIndex, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]]

clf = tree.DecisionTreeClassifier()
clf = clf.fit(X, Y)
#print(clf.predict([[62, 0, 0, 138, 294, 1, 1, 106, 0, 1.9, 1, 3, 2]]))
#########################   TEST ##########################
right = 0
wrong = 0
for i in range(testIndex, len(age)):
    predict = clf.predict([testDF.loc[i]])
    if(predict>0.5 and target[i] == 1):
        right+=1
    if(predict<0.5 and target[i] == 0):
        right+=1
    if(predict>0.5 and target[i] == 0):
        wrong+=1
    if(predict<0.5 and target[i] == 1):
        wrong+=1
print(right, wrong)
print(right/(right+wrong))
###########################################################
#   5 groups of 150
groups = []
for i in range(0, 5):
    group = []
    for i in range(0, 150):
        index = random.randint(0, trainIndex)
        group.append(df.loc[index])
    groups.append(group)
#   Training each bag
groupsCLF = []
for i in range(0, 5):
    df1 = pd.DataFrame(groups[i])
    X1 = df1.iloc[:, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]]
    Y1 = df1.iloc[:, [13]]
    c = tree.DecisionTreeClassifier()
    c = c.fit(X1, Y1)
    groupsCLF.append(c)
#########################   TEST ##########################
right = 0
wrong = 0
for i in range(testIndex, len(age)):
    b = []
    for j in range(0, 5):
        a = groupsCLF[j].predict([testDF.loc[i]])
        b.append(a)
    b = np.array(b)
    if(np.mean(b)>0.5 and target[i] == 1):
        right+=1
    if(np.mean(b)<0.5 and target[i] == 0):
        right+=1
    if(np.mean(b)>0.5 and target[i] == 0):
        wrong+=1
    if(np.mean(b)<0.5 and target[i] == 1):
        wrong+=1
print(right, wrong)
print(right/(right+wrong))
###########################################################
features = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
test = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
for k in range(0, 12):
    groupsCLF = []

    droped = test[0]
    test.pop(0)
    features.pop(0)
    for x in range(0, 5):
        df1 = pd.DataFrame(groups[x])
        X1 = df1.iloc[:, features]
        Y1 = df1.iloc[:, [13]]
        c = tree.DecisionTreeClassifier()
        c = c.fit(X1, Y1)
        groupsCLF.append(c)
    #########################   TEST ##########################
    right = 0
    wrong = 0
    for i in range(testIndex, len(age)):
        b = []
        for j in range(0, 5):
            a = groupsCLF[j].predict([(testDF.drop([test[0]], axis=1)).loc[i]])
            b.append(a)
        b = np.array(b)
        if(np.mean(b)>0.5 and target[i] == 1):
            right+=1
        if(np.mean(b)<0.5 and target[i] == 0):
            right+=1
        if(np.mean(b)>0.5 and target[i] == 0):
            wrong+=1
        if(np.mean(b)<0.5 and target[i] == 1):
            wrong+=1
    print(droped, right, wrong)
    print(right/(right+wrong))
    ###########################################################
    features.append(k)
    test.append(droped)
