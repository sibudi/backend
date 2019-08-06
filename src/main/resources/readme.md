
电子签章剩余步骤
====
- 待digiSign 测试通过给我们token和kuser信息
 ```java
  token就是一个验证账号，kuser是我们公司自动签名的时候再digisign生成的信息
  (发送签约文档的时候需要)
  在我们的bootstrap-prd.yml中修改（third digiSign下 token 和automaticSignKUser）
```

- 开关打开
```java
 redis 中将digital:sign:switch 设置为 true
```

- 发布task，risk, api, management



配合测试步骤
====
- 如果digiSign需要测试的话可能需要我们改数据或者做其他事情,配合测试主要做入如下步骤
```java
他们出单后我们在ApiApplication直接调用如下代码：
        ContractSignService service = ctx.getBean(ContractSignService.class);
        OrdService ordService = ctx.getBean(OrdService.class);
        OrdOrder order = ordService.getOrderByOrderNo("订单号");
        service.changeStatusAfterOrderPass(order);
        
    asli ekyc 很难通过，所以这个代码调用第一次的时候基本不过，
    需要将usrSignContractStep     signStep =1 的对应的stepResult 值改为1
    然后重新调用一次
    (因为要读取自拍照和身份证照片，所以最好是打包后放到测试环境跑)
```

