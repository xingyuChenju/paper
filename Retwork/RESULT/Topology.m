% path = "ColorWave/";
% color_col = 1;
% Rtime_col = 2;
% Qtime_col = 3;
% path = "KCoverage";
% path = "Reduntant";
path = "Topology/";
num_col = 1;
Qtime_col = 2;
Rtime_col =3;
all_col =4;
fn_col =5;
fp_col =6;
data = load(path+"result6_2.txt")
a = 1000:1000:7000
data1 = data(:,[num_col Qtime_col Rtime_col]);
data2 = data1(:,[2 3])./data(:,1);

% all = sum(data(:,all_col));
% fn = sum(data(:,fn_col));
% fp = sum(data(:,fp_col));
% fnr = fn/all;
% fpr = fp/all;