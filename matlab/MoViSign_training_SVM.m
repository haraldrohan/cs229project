% SVM built on CVX
function [w b] = MoViSign_training_SVM(X, y)
%function alpha = MoViSign_training_SVM(X, y)
n = size(X, 1);
m = size(X, 2);


% X is a n-by-m matrix, n is the number of features
%   and m is the number of training examples
%X = zeros(n, m);

% y is a 1-by-m vector, with each entry being 1 or -1
%y = ones(1, m);
C = 1;
alpha = 0;
% cvx_begin
%     variable alpha(m);
%     maximize sum(alpha) - (1/2)*sum(sum((y'*y).*(alpha' * alpha).*(X' * X)));
%     subject to
%         % t <= sum(alpha) - (1/2)*sum(sum((y'*y).*(alpha' * alpha).*(X' * X)));
%         alpha >= 0;
%         alpha <= C;
%         y * alpha == 0;
% cvx_end

% disp(alpha);
% disp(t);

cvx_begin
    variables b w(n);
    minimize norm(w);
    subject to
        for i=1:m
            y(i) * (w'*X(:,i) + b) >= 1;
        end
cvx_end
