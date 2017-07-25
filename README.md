# goodsnackbar
仿照 snackbar 写的自定义弹出框 效果图如下

 ![image](https://raw.githubusercontent.com/small-tree/goodsnackbar/master/iamge/11111.gif)
 
 ## 使用
```
final View inflate = View.inflate(this, R.layout.mysnackbar_layout, null);
Button bt_action = (Button) inflate.findViewById(R.id.bt_action);
final GoodSnackbar instance = GoodSnackbar.make(this.viewById) // 通过一个父view创建GoodSnackbar对象
       .setMyView(inflate) // 设置GoodSnackbar的内容
       .setDuration(2500); // 设置GoodSnackbar 显示的时间长度
       
switch (((Button) view).getText().toString()) {
    case "top":
        instance.setWhereFrom(GoodSnackbar.From.TOP); //设置 GoodSnackbar的显示位置
        break;
    case "bottom":
        instance.setWhereFrom(GoodSnackbar.From.BOTTOM);
        break;
}
bt_action.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        instance.close(); // 隐藏 GoodSnackbar
    }
});
instance.show();  // 显示 GoodSnackbar
```
