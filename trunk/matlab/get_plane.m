% input a: nx4 matrix, the columns are (t,x,y,z)
% output theta: matrix of 4x1 co-efficients of the best-fit plane
function theta = get_plane(a)
n = length(a(:,1));
b = [ones(n,1) a(:,2:3)];
c = a(:,4);
theta = [b\c;1];
end