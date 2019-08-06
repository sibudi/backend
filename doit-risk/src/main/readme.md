
===

新规则测试：
1) 把新开发的规则放到rules.wangwang.test目录下的test.drl中
2) 在RiskApplication 中直接调用RuleTestService.test(); 需要自己组装测试数据
3) 由于现在对用户进行二次审核(先审核一次，符合条件按照申请金额放款，不符合条件的看是否符合100rmb产品，进行降额处理)，
   新增规则的时候要考虑这个规则是通用的还是100rmb的。
   1) 如果是新增通用的规则，设置appliedTo为3(android,ios 都执行)，specifiedProduct=0，所有产品都会执行
   2) 如果是新增针对100RMB的产品，规则设置同上，但是规则写在 100RMBProduct.drl中
   3) 如果是已经有的规则改变阈值，将原有规则specifiedProduct字段设置为1(表中设置)，然后新增一条新阈值的规则


sysAutoReviewRule表：ruleType说明
=== 
- 1:黑名单
- 2:用户信息
- 3:通讯录信息
- 4:通话记录信息
- 5:短信
- 6:手机安装app
- 7:设备信息
- 8:tokopedia
- 9:gojek
- 10:facebook
- 11:初审拒绝
- 12：复审拒绝
- 15：组合规则等【新增的规则很多没有归类统一放到着一类了】
- 20：复借规则
- 21：免电核规则
- 22：免初审、复审规则
- 23: 特殊规则
- 24：欺诈黑名单相关
- 25：外呼规则
- 26: 更改产品特殊规则
- 27: 模型分

```
另外 复借的规则一般都包含MULTI or REBORROW
```
specifiedProduct 字段
---
改字段说明规则对那些产品排除放开（1: 标识对100rmb产品放开的规则，0：默认值-忽略不考虑其含义 ）
```java
   @Getter
    public enum ExcludedForSpecifiedProduct {
        DEFAULT(0),
        PRODUCT_100RMB(1);
        ExcludedForSpecifiedProduct(int code) {
            this.code = code;
        }
        private int code;
    }
```
appliedTo 字段
--
改字段描述的是规则适用于那些情景，具体值说明如下(1,2,3,14,15等取值)
```java

 @Getter
    public enum AppliedTargetEnum {
        //规则适用类型
        android(1),
        iOS(2),
        both(3),
        android_CashCash(14),//android and cashcash执行
        iOS_CashCash(24),//  ios and cashcash执行
        both_CashCash(34),//只要是cashcash就执行，不区分ios android
        android_NORMAL(15),// android and 自有渠道
        iOS_NORMAL(25),// android and 自有渠道
        both_NORMAL(35), // 自有渠道[和CashCash渠道的分开]
        ;
```




规则评估
====
- 单个规则拒绝率


- 每日拒绝规则占比变化情况


开发流程
======
1、sysAutoReviewRule 增加规则记录
2、往flowRuleSet 表增加规则属于哪个流程(600,免核，打标签等..FlowEnum)
3、往ruleParam规则增加参数记录【这几步可以参考】
4、编写drl文件(在resources/rules/doit目录下面)

