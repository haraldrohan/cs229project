%cd('./data/flat');
file_list = ls('./data/king/merge*');

X = [];
y = [];
[str remained] = strtok(file_list);
num_clusters = 16;

while(strcmp(str,'')==0)
    file_list = remained;
    file_name = strcat('./data/king/',str);
    data = load(str);
    [a r] = strtok(str,'_');
    while( strcmp(a,'true')==0 && strcmp(a,'false')==0)
            [a r] = strtok(r,'_');
    end
    
    if( strcmp(a,'true') )
        y = [y 1];
    else
        y = [y -1];
    end
    
    accx = data(:,1); 
    accy = data(:,2); 
    accz = data(:,3);    
    acca = data(:,4); 
    accr = data(:,5); 
    accp = data(:,6);  
    timestamp = data(:,7) - data(1,7);
    
    sizet = size(timestamp, 1);
    timediff = [timestamp(2:sizet); 1];
    timediff = (timediff - timestamp)./1000;
    %t = timestamp;
    %timediff = ([t(2:sizet); t(sizet)]+t)./2 - (t+[t(1); t(1:sizet-1)])./2;
    %timediff = timediff ./ 1000;
    
%     new_data = zeros(sizet, 3);
%     velocity = new_data;
%     for i=2:sizet
%         velocity(i,:) = velocity(i-1,:) + [accx(i-1) accy(i-1) accz(i-1)] * (timediff(i-1));
%         new_data(i,:) = new_data(i-1,:) + velocity(i-1,:) * timediff(i-1);
%         velocity(i,:) = velocity(i-1,:) + ([accx(i-1) accy(i-1) accz(i-1)] + [accx(i) accy(i) accz(i)]) * (timediff(i-1)) ./ 2;
%         new_data(i,:) = new_data(i-1,:) + (velocity(i-1,:) + velocity(i,:)) * (timediff(i-1)) ./ 2;
%     end
%     figure;
%     subplot(3,3,1); plot(new_data(:,1), timestamp);
%     subplot(3,3,2); plot(new_data(:,2), timestamp);
%     subplot(3,3,3); plot(new_data(:,3), timestamp);
%     subplot(3,3,4); plot(velocity(:,1), timestamp);
%     subplot(3,3,5); plot(velocity(:,2), timestamp);
%     subplot(3,3,6); plot(velocity(:,3), timestamp);
%     subplot(3,3,7); plot(accx, timestamp);
%     subplot(3,3,8); plot(accy, timestamp);
%     subplot(3,3,9); plot(accz, timestamp);
%     
%     new_data = [timestamp data(:,1:3)];
%     timestamp = timestamp ./ max(timestamp);
%     new_data = [timestamp new_data];
%     aligned_mat = get_projection(new_data);
%     aligned_mat = [aligned_mat(:,2:3) aligned_mat(:,1)];
    timestamp = timestamp ./ max(timestamp);
    aligned_mat = [timestamp data(:, 1:6)];
    %assigned_cluster = kmeans(aligned_mat, num_clusters);
    
    assigned_cluster = ceil((1:sizet)./(sizet / num_clusters));
    
    
    avg_time = [];
    for i=1:num_clusters
        avg_time = [avg_time mean(timestamp(assigned_cluster == i))];
    end
    [B IX] = sort(avg_time);

    ordered_mat = [];
    for i=1:num_clusters
    %ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),1))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),2))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),3))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),4))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),5))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),6))];
    ordered_mat = [ordered_mat mean(aligned_mat(assigned_cluster == IX(i),7))];

    end
    X = [X ordered_mat'];
    
    [str remained] = strtok(file_list);
end

n = size(X, 1);
m = size(X, 2);

result = [];
for i = 2:m-1
trainX = [X(:, 1:i-1) X(:, i+1:m)];
testX = X(:, i);
trainY = [y(:, 1:i-1) y(:, i+1:m)];
testY = y(:, i);

%alpha = MoViSign_training_SVM(trainX, trainY);
%[b alphas] = svm_train(trainX, trainY);
%sum(alpha .* trainY' .* (trainX' * testX))

[w b] = MoViSign_training_SVM(trainX, trainY);
w
b
result = [result w'*testX + b];
end

[result; y(2:m-1)]

