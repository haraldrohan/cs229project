%input m: some matrix
%output a: m after column-wise normalization such that in each column, max
%=1 and min=0
function a = normalize(m)
a = m;
n = length(a(:,1));
min_a = min(a);
diff_a = max(a) - min_a;
a = (a - ones(n,1)*min_a) ./ (ones(n,1)*diff_a);
end