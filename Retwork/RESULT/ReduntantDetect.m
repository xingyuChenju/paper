path = "RedundantDetect\";
% 29780 3361 3341 4 0 1;
allTime_col = 1;
RCost_col = 2;
tagNum_col =3;
all_col =4;
fn_col =5;
fp_col =6;
data = load(path+"result8antennas_1.txt");
all = sum(data(:,all_col));
fp = sum(data(:,fp_col))/all
