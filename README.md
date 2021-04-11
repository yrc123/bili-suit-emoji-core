# bili-suit-emojy

## 简介

bili-suit-emojy是一款用来下载b站套装图片的软件

使用了WebMagic爬虫框架

## 用法

```
usage: bili套装表情下载器
 -d,--directory <arg>   指定放置生成的类文件的位置
 -h,--help              将此帮助消息输出到输出流
 -i,--id <arg>          待爬取的主题item_id（即分享链接后的item_id的值）
                        url与id输入一个即可
 -u,--url <arg>         待爬取的主题分享链接url
                        url与id输入一个即可
```

例如

```
bili.jar -u "https://www.bilibili.com/h5/mall/suit/detail?navhide=1&id=4389"
```

