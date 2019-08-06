
update sysAutoReviewRule u set u.ruleData = concat(ruleData,'#Kabupaten Serang#Kota Serang#Kota Yogyakarta#Kota Surabaya#Kota Pontianak#Kota Makassar#Kota Palembang#Kota Medan')
where u.ruleDetailType in ('ORDER_ADDRESS_INVALID','LIVE_ADDRESS_INVALID','WORK_ADDRESS_INVALID','SCHOOL_ADDRESS_INVALID','ADDRESS_INVALID')
and disabled=0;

update sysAutoReviewRule u set u.ruleData = concat(ruleData,'#0254#0274#031#0561#0411#0711#061#254#274#31#561#411#711#61')
where u.ruleDetailType ='COMPANY_TEL_NOT_IN_JAKARTA' and disabled=0;


commit;
