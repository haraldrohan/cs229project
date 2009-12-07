%input m: nx3 matrix of points lying on a plane passing through the origin
%input norm_vec: a vector normal to the plane (starting from the origin)
%output aligned_mat: nx2 matrix, after rotating the plane to align with the
% xy-plane, and rotating the points to align the regression line with the
% x-axis
function aligned_mat = align(m, norm_vec)
n = length(m(:,1));
% convert norm_vec to a unit vector
norm_vec = norm_vec/norm(norm_vec);
a = norm_vec(1);
b = norm_vec(2);
c = norm_vec(3);
d = sqrt(b^2 + c^2);
% the magic rotation matrix
rot_mat = [d (-a*b/d) (-a*c/d);0 (c/d) (-b/d);a b c];
aligned_mat = (rot_mat*m')';
% trim the matrix to remove the z-values
aligned_mat = aligned_mat(:,1:2);
% find the regression-line
theta = [[ones(n,1) aligned_mat(:,1)]\aligned_mat(:,2);1];
% find the regression-line angle
alpha = 0;
% the signature should always be in the positive x-direction
if(aligned_mat(1,1) > aligned_mat(n,1))
    alpha = pi;
end
alpha = alpha - atan(theta(3)/theta(2));
% rotate the matrix to align the regression-line with positive x-axis
rot_mat = [cos(alpha) sin(alpha);-sin(alpha) cos(alpha)];
aligned_mat = (rot_mat*aligned_mat')';
% turn the signature upside down if the final point is below the first
% point
if(aligned_mat(1,2) > aligned_mat(n,2))
    aligned_mat = ([1 0;0 (-1)]*aligned_mat')';
end
end