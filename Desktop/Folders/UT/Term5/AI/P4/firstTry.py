from sklearn import tree
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
import random

# Create a random subsample from the dataset with replacement
def subsample(dataset, ratio=1.0):
	sample = list()
	n_sample = round(len(dataset) * ratio)
	while len(sample) < n_sample:
		index = random.randrange(len(dataset))
		sample.append(dataset.loc[index])
	return sample

df = pd.read_csv('data.csv')
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
Y = df.iloc[:, [13]]
X = df.iloc[:, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]]

clf = tree.DecisionTreeClassifier()
clf = clf.fit(X, Y)
#print(clf.predict([[62, 0, 0, 138, 294, 1, 1, 106, 0, 1.9, 1, 3, 2]]))
#########################   test the Decision Tree #############################################
#   5 groups of 150
groups = []
for i in range(0, 5):
    group = []
    for i in range(0, 150):
        index = random.randint(0, len(age)-1)
        group.append(df.loc[index])
    groups.append(group)
