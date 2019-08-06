update sysAutoReviewRule u set u.ruleData = concat(ruleData,'#Kabupaten Sleman#Kota Balikpapan#Kota Samarinda#Kabupaten Gresik#Kota Jambi#Kota
Magelang#Kabupaten Magelang#Kabupaten Banyuwangi#Kabupaten Wonogiri#Kabupaten Banyumas#Kabupaten Garut#Kabupaten Jember')
where u.ruleDetailType in ('ORDER_ADDRESS_INVALID','LIVE_ADDRESS_INVALID','WORK_ADDRESS_INVALID','SCHOOL_ADDRESS_INVALID','ADDRESS_INVALID')
      and disabled=0;

update sysAutoReviewRule u set u.ruleData = concat(ruleData,
'#0274#274#0543#543#0541#541#031#31#0741#741#0293#293#0333#333#0273#273#0281#281#0262#262#0331#331')
where u.ruleDetailType ='COMPANY_TEL_NOT_IN_JAKARTA' and disabled=0;