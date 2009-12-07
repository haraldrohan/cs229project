%input filename: name of the file to load the nx4 matrix from
%output a: nx3 matrix with points aligned with xy-plane, and normalized
function a = get_projection(m)
% read the matrix
%m = load(filename);
% get the regression plane
theta = get_plane(m);
% get rid of the intercept term, so that the plane passes through the
% origin
theta(1) = 0;
% project the points on the plane
m = project(m, theta);
% align the points to xy-plane
a = align(m(:,2:4), theta(2:4));
% add the time info
a = [m(:,1) a];
% normalize the whole data;
a = normalize(a);
end






