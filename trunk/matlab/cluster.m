function clusters = clustering(data, K)
data = load('movisign/flat/Orientation_Default_true_1258745716796.log');
clusters = kmeans(data, K);

