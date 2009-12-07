function output = f(input_x, x, y, alpha, b)
    output = 0;
    numTrainDocs = size(x, 1);
    for i = 1:numTrainDocs
        output = output + alpha(i) * y(i) * inner(x(i,:), input_x);
    end
    output = output + b;