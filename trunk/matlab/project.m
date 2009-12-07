
%input m: matrix of nx4 points, the columns are (t,x,y,z)
%input theta: matrix of 4x1 coefficients of the best fit plane
%output a: matrix of nx4 points projected on the plane
function a = project(m, theta)
n = length(m(:,1));
a = m;
a(:,1) = ones(n,1);
coeff_col = a*theta/(norm(theta)-1);
a = a - coeff_col*theta';
a(:,1) = m(:,1);
end