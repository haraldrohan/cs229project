% cs229 hw1 1(b)
X;
y;

%%%%%

% X = X';
% y = y';

%%%%%

m = size(X, 1);
n = size(X, 2);
x = [ones(m,1) X];
theta = ones(n+1, 1);
theta_new = zeros(n+1, 1);

epsilon = 10^-3;

while norm(theta_new - theta) > epsilon
    theta = theta_new;
    hermission = zeros(n+1, n+1);
    gradient_likelihood = zeros(n+1, 1);
    for k=1:m;
        gradient_likelihood = gradient_likelihood + (y(k)*(1-h(x(k,:), theta)) - (1-y(k))*h(x(k,:), theta)).*(x(k,:)');
        hermission = hermission - h(x(k,:), theta)*(1-h(x(k,:), theta))*(x(k,:)' * x(k,:));
    end
    theta_new = theta - inv(hermission) * gradient_likelihood;
    % disp(norm(theta_new - theta));
end
theta = theta_new;

disp(theta_new);

% 1(c)
figure(1)
hold;
for k=1:m
    if y(k) == 0
        plot(X(k,1), X(k,2), 'o');
    else
        plot(X(k,1), X(k,2), 'x');
    end
end

theta_x = 0:0.01:8;
theta_y = (-theta(1)-theta(2).*theta_x)./theta(3);
plot(theta_x, theta_y);

axis([0 8 -5 4]);
