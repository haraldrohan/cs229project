function [b, alphas] = svm_train(X, Y)

% trainMatrix is now a (numTrainDocs x numTokens) matrix.
% Each row represents a unique document (email).
% The j-th column of the row $i$ represents the number of times the j-th
% token appeared in email $i$. 

% tokenlist is a long string containing the list of all tokens (words).
% These tokens are easily known by position in the file TOKENS_LIST

% trainCategory is a (numTrainDocs x 1) vector containing the true 
% classifications for the documents just read in. The i-th entry gives the 
% correct class for the i-th email (which corresponds to the i-th row in 
% the document word matrix).

% Spam documents are indicated as class 1, and non-spam as class 0.
% Note that for the SVM, you would want to convert these to +1 and -1.

% YOUR CODE HERE

% Hint: If you implement the smo algorithm in smo_train.m, then you can use
% smo_verify.m to test your implementation on a small data set, rather
% than all the emails.


C = 1;
tol = 0.001;
max_passes = 10;
x=X;
y=Y';
numTrainDocs = size(X, 1);
numTokens = size(X, 2);

alpha = zeros(numTrainDocs, 1);
b = 0;
passes = 0;


while(passes < max_passes)
    num_changed_alphas = 0;
    for i = 1:numTrainDocs
        Ei = f(x(i,:), x, y, alpha, b) - y(i);
        if ((y(i)*Ei < -tol) && (alpha(i) < C)) || ( (y(i)*Ei > tol) && (alpha(i) > 0))
            j = int64(rand(1, 1) * numTrainDocs);
            while ( j == i || j == 0)
                j = int64(rand(1, 1) * numTrainDocs);
            end
            Ej = f(x(j,:), x, y, alpha, b) - y(j);
            alpha_old = alpha;
            if( y(i) ~= y(j))
                L = max(0, (alpha(j) - alpha(i)));
                H = min(C, (C+alpha(j)-alpha(i)));
            else
                L = max(0, (alpha(i) + alpha(j) - C));
                H = min(C, (alpha(i)+alpha(j)));
            end
            
            if L == H
                continue
            end
            
            eta = 2* inner(x(i,:),x(j,:)) - inner(x(i,:),x(i,:)) - inner(x(j,:),x(j,:));
            if eta >=0
                continue
            end
            
            alpha(j) = alpha(j) - (y(j) * (Ei - Ej) / eta);
            if alpha(j) > H
                alpha(j) = H;
            elseif alpha(j) < L
                alpha(j) = L;
            end
            
            if(abs(alpha(j) - alpha_old(j)) < 1e-5)
                continue
            end
            
            alpha(i) = alpha(i) + y(i) * y(j) * (alpha_old(j) - alpha(j));
            
            b1 = b - Ei - y(i) * (alpha(i)-alpha_old(i)) * inner(x(i,:),x(i,:)) - y(j) * (alpha(j)-alpha_old(j)) * inner(x(i,:),x(j,:));
            b2 = b - Ej - y(i) * (alpha(i)-alpha_old(i)) * inner(x(i,:),x(j,:)) - y(j) * (alpha(j)-alpha_old(j)) * inner(x(j,:),x(j,:));
            
            if alpha(i) > 0 && alpha(i) < C
                b = b1;
            elseif alpha(j) > 0 && alpha(j) < C
                b = b2;
            else
                b = (b1+b2)/2;
            end
            
            num_changed_alphas = num_changed_alphas + 1;
        end 
    end
    if num_changed_alphas == 0
        passes = passes + 1;
    else
        passes = 0;
    end
end

alphas = alpha;
