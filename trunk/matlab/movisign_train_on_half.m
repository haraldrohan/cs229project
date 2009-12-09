basic_dir = './data/king_train_new/';

file_list = ls(strcat(basic_dir, 'merge*'));

[str remained] = strtok(file_list);
num_clusters = 3;
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

positive_index = (y == 1);
positiveX = X(:,positive_index);
positiveY = y(:,positive_index);
negative_index = (y == -1);
negativeX = X(:,negative_index);
negativeY = y(:,negative_index);

num_division = 20;
pos_idx_step = size(positiveX, 2)/num_division;
neg_idx_step = size(negativeX, 2)/num_division;
positive_train_index = floor(pos_idx_step*(num_division-1));
negative_train_index = floor(neg_idx_step*(num_division-1));
%disp(positive_train_index + negative_train_index);

result = [];
reference = [];
for i=1:num_division
    disp(i);
    pos_idx_start = floor(pos_idx_step * (i-1));
    if pos_idx_start < 2
        pos_idx_start = 2;
    end
    pos_idx_end = floor(pos_idx_step * i);
    if pos_idx_end > m-1
        pos_idx_end = m-1;
    end
    neg_idx_start = floor(neg_idx_step * (i-1));
    if neg_idx_start < 2
        neg_idx_start = 2;
    end
    neg_idx_end = floor(neg_idx_step * i);
    if neg_idx_end > m-1
        neg_idx_end = m-1;
    end
    
    trainX = [positiveX(:,1:pos_idx_start-1) positiveX(:,pos_idx_end+1:size(positiveX,2)) negativeX(:,1:neg_idx_start-1) negativeX(:,neg_idx_end+1:size(negativeX,2))];
    trainY = [positiveY(:,1:pos_idx_start-1) positiveY(:,pos_idx_end+1:size(positiveY,2)) negativeY(:,1:neg_idx_start-1) negativeY(:,neg_idx_end+1:size(negativeY,2))];
    testX = [positiveX(:,pos_idx_start:pos_idx_end) negativeX(:,neg_idx_start:neg_idx_end)];
    testY = [positiveY(:,pos_idx_start:pos_idx_end) negativeY(:,neg_idx_start:neg_idx_end)];

    [w b] = MoViSign_training_SVM(trainX, trainY);
    disp(w);
    disp(b);
    result = [result w'*testX + b];
    reference = [reference testY];
end

% comparison = [result; y(2:m-1)];
comparison = [result; reference];
error_rate = sum((comparison(1,:) .* comparison(2,:)) < 0) / size(comparison, 2);
disp(error_rate);

negative = comparison(1 , comparison(2, :) == -1);
false_positive_rate = sum(negative > 0) / size(negative, 2);
disp(false_positive_rate);

positive = comparison(1 , comparison(2, :) == 1);
false_negative_rate = sum(positive < 0) / size(positive, 2);
disp(false_negative_rate);



