basic_dir = './data/liu_train/';

file_list = ls(strcat(basic_dir, 'merge*'));

[str remained] = strtok(file_list);
num_clusters = 32;
num_files = size(file_list, 1);
X = zeros(num_clusters*6,num_files);
y = zeros(1,num_files);
file_index = 1;

while(file_index < num_files)
    str = file_list(file_index,:);
    str = strtok(str, ' ');
    file_name = strcat(basic_dir,str);
    data = load(strcat(basic_dir ,str));
    [a r] = strtok(str,'_');
    while( strcmp(a,'true')==0 && strcmp(a,'false')==0)
            [a r] = strtok(r,'_');
    end
    
    if( strcmp(a,'true') )
        y(file_index) = 1;
    else
        y(file_index) = -1;
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

    timestamp = timestamp ./ max(timestamp);
    aligned_mat = [timestamp data(:, 1:6)];
    
    assigned_cluster = ceil((1:sizet)./(sizet / num_clusters));
    
    
    avg_time = zeros(1,num_clusters);
    for i=1:num_clusters
        avg_time(i) = mean(timestamp(assigned_cluster == i));
    end
    [B IX] = sort(avg_time);

    ordered_mat = zeros(1, num_clusters*6);
    for i=1:num_clusters
    ordered_mat(6*(i-1)+1) = mean(aligned_mat(assigned_cluster == IX(i),2));
    ordered_mat(6*(i-1)+2) = mean(aligned_mat(assigned_cluster == IX(i),3));
    ordered_mat(6*(i-1)+3) = mean(aligned_mat(assigned_cluster == IX(i),4));
    ordered_mat(6*(i-1)+4) = mean(aligned_mat(assigned_cluster == IX(i),5));
    ordered_mat(6*(i-1)+5) = mean(aligned_mat(assigned_cluster == IX(i),6));
    ordered_mat(6*(i-1)+6) = mean(aligned_mat(assigned_cluster == IX(i),7));

    end
    X(:,file_index) = ordered_mat';
    
    file_index = file_index + 1;
end

n = size(X, 1);
m = size(X, 2);

% result = zeros(1, m-2);
% for i = 2:m-1
%     trainX = [X(:, 1:i-1) X(:, i+1:m)];
%     testX = X(:, i);
%     trainY = [y(:, 1:i-1) y(:, i+1:m)];
%     testY = y(:, i);
% 
%     [w b] = MoViSign_training_SVM(trainX, trainY);
%     disp(w);
%     disp(i);
%     disp(b);
%     result(i-1) = w'*testX + b;
% end

comparison = [result; y(2:m-1)];
error_rate = sum((comparison(1,:) .* comparison(2,:)) < 0) / size(comparison, 2);
disp(error_rate);

negative = comparison(1 , comparison(2, :) == -1);
false_positive_rate = sum(negative > 0) / size(negative, 2);
disp(false_positive_rate);

positive = comparison(1 , comparison(2, :) == 1);
false_negative_rate = sum(positive < 0) / size(positive, 2);
disp(false_negative_rate);



