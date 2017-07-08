#!/bin/bash

node="twitter"
domain="localhost"
port="9200"

es() {
  method=$1
  path=$2
  json=$3
  curl -X$method http://$domain:$port/$node/$path -d "$json"
  echo ""
}

# Message
echo ""
echo "//====================================//"
echo "//   SAW Elasticsearch ReplaceScheme  //"
echo "//====================================//"
echo ""
echo "connect to http://$domain:$port/$node"
echo ""

# ===================================================================================
#                                                                               Reset
#                                                                               =====
echo "<==== Reset ====>"
echo ""

# Reset
curl -XDELETE http://$domain:$port/*
echo ""
curl -XPOST http://$domain:$port/$node
echo ""

# ===================================================================================
#                                                                             Mapping
#                                                                             =======
echo ""
echo "<==== Mapping ====>"
echo ""

es PUT _mapping/member '
{
  "member": {
    "_index" : {"enabled": true},
    "_timestamp": {
      "enabled": true,
      "store": true
    },
    "properties": {
      "mail": {
        "index": "not_analyzed",
        "type": "string"
      },
      "password": {"type": "string"},
      "confirm": {
        "type": "boolean",
        "default": false
      },
      "confirmed": {
        "type": "boolean",
        "default": false
      }
    }
  }
}'

es PUT _mapping/memberWithdrawal '
{
  "memberWithdrawal": {
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "memberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "reason": {"type": "string"}
    }
  }
}'

es PUT _mapping/memberConfirmHash '
{
  "memberConfirmHash": {
    "properties": {
      "memberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "confirmHash": {"type": "string"},
      "used": {
        "type": "boolean",
        "default": false
      }
    }
  }
}'

es PUT _mapping/tweet '
{
  "tweet": {
    "_id": {"store": true},
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "memberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "shareContentsSurfaceUrl": {"type": "string"},
      "shareContentsId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "comment": {"type": "string"},
      "reTweetFromId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "deleted": {
        "type": "boolean",
        "default": false
      }
    }
  }
}'

es PUT _mapping/tweetValue '
{
  "tweetValue": {
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "valueFromMemberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "valueToMemberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "valueToTweetId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "valueScore": {
        "type": "integer"
      }
    }
  }
}'

es PUT _mapping/shareContents '
{
  "shareContents": {
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "url": {
        "index": "not_analyzed",
        "type": "string"
      },
      "thumbnailUrl": {type: "string"},
      "title": {"type": "string"}
    }
  }
}'

# ===================================================================================
#                                                                         Insert Data
#                                                                         ===========
echo ""
echo "<==== Insert ====>"
echo ""

# password: Bison
es POST member/1 '
{
  "mail": "bison@streetfighter.org",
  "password": "18b962f50525d6b098409574c2392e7d458b1a9d1cf67759d4d1ddb075cc8177b33a9b215b335ab0b0414371c1b4c3d2033fb709fce6ac38fd90b94107165d7c",
  "confirmed": true
}'

# password: Barlog
es POST member/2 '
{
  "mail": "barlog@streetfighter.org",
  "password": "212324398bf0068f6e4a927cdc3951ba14992099979ef36e784a416119c323cef67a461fcdabc38b3d54d0f2db0a6362261ea74d08d1476358d5d84a31f07241",
  "confirmed": true
}'

# password: Sagat
es POST member/3 '
{
  "mail": "sagat@streetfighter.org",
  "password": "ef135173d9666b180e6bd50a74784486e2fd536eb829188b95ebf16f7572cf7811ce77c782be647fbc0da7d103f660ca6bcbdb8f4652da37a9cb1910be3457c9",
  "confirmed": true
}'

# password: Vega
es POST member/4 '
{
  "mail": "vega@streetfighter.org",
  "password": "bab45847e2227d0c57136825dc31ba89c450bb522f62864f3d6caee24ef36962a2f89772161335252473da18b5828bfb9f286e2a618e49e0dac1d16f6ba24904",
  "confirmed": true
}'

# password: Vega
es POST member/5 '
{
  "mail": "ryu@streetfighter.org",
  "password": "bab45847e2227d0c57136825dc31ba89c450bb522f62864f3d6caee24ef36962a2f89772161335252473da18b5828bfb9f286e2a618e49e0dac1d16f6ba24904",
  "confirmed": true
}'

# Insert memberWithdrawal Data
es POST memberWithdrawal '
{
  "memberId": "5",
  "reason": "俺より強い奴がいなくなったから"
}'

# Insert shareContents Data
es POST shareContents/1 '
{
  "url": "http://www.bizreach.co.jp/",
  "thumbnailUrl": "http://capture.heartrails.com/small?http://www.bizreach.co.jp/",
  "title": "株式会社ビズリーチ(BizReach)【公式ホームページ】"
}'

es POST shareContents/2 '
{
  "url": "https://jp.stanby.com/",
  "thumbnailUrl": "http://capture.heartrails.com/small?https://jp.stanby.com/",
  "title": "スタンバイ 日本最大級の求人検索エンジン"
}'

es POST shareContents/3 '
{
  "url": "https://www.bizreach.jp/",
  "thumbnailUrl": "http://capture.heartrails.com/small?https://www.bizreach.jp/",
  "title": "転職・求人情報サイトのビズリーチ | 選ばれた人だけの会員制転職サイト「BizReach（ビズリーチ）」"
}'

es POST shareContents/4 '
{
  "url": "https://www.careertrek.com/",
  "thumbnailUrl": "http://capture.heartrails.com/small?https://www.careertrek.com/",
  "title": "キャリアトレック[careertrek]｜レコメンド型転職サイト"
}'

es POST shareContents/5 '
{
  "url": "https://29reach.com/",
  "thumbnailUrl": "http://capture.heartrails.com/small?https://29reach.com/",
  "title": "ニクリーチ2016 | お腹を空かせた学生のための肉食就活サイト-"
}'

# Insert tweet Data
es POST tweet/1 '
{
  "memberId": "1",
  "shareContentsSurfaceUrl": "http://www.bizreach.co.jp/",
  "shareContentsId": "1",
  "comment": "インターネットの力で、世の中の選択肢と可能性と広げていく",
  "deleted": false
}'

es POST tweet/2 '
{
  "memberId": "2",
  "shareContentsSurfaceUrl": "http://www.bizreach.co.jp/",
  "shareContentsId": "1",
  "comment": "Work Hard, Play SUPER Hard",
  "deleted": true
}'

es POST tweet/3 '
{
  "memberId": "3",
  "shareContentsSurfaceUrl": "https://jp.stanby.com/",
  "shareContentsId": "2",
  "comment": "すごい便利、めっちゃ使える",
  "deleted": false
}'

es POST tweet/4 '
{
  "memberId": "1",
  "shareContentsSurfaceUrl": "https://jp.stanby.com/",
  "shareContentsId": "2",
  "comment": "ほんとそれ",
  "deleted": false
}'

es POST tweet/5 '
{
  "memberId": "4",
  "shareContentsSurfaceUrl": "http://www.bizreach.co.jp/",
  "shareContentsId": "3",
  "comment": "エグゼクティブなポジションを探すのに便利でした",
  "deleted": false
}'

es POST tweet/6 '
{
  "memberId": "5",
  "shareContentsSurfaceUrl": "https://29reach.com/",
  "shareContentsId": "5",
  "comment": "うまい肉に会いに行く",
  "deleted": true
}'

es POST tweet/7 '
{
  "memberId": "2",
  "shareContentsSurfaceUrl": "https://goo.gl/zbgQan",
  "shareContentsId": "4",
  "comment": "人工知能で対戦相手をマッチングしてくれました",
  "deleted": true
}'

es POST tweet/8 '
{
  "memberId": "3",
  "shareContentsSurfaceUrl": "http://u222u.info/ngFJ",
  "shareContentsId": "4",
  "comment": "そういう機能はないです",
  "deleted": false
}'

# Insert tweetValue Data
es POST tweetValue '
{
  "valueFromMemberId": "1",
  "valueToMemberId": "2",
  "valueToTweetId": "2",
  "valueScore": 1
}'

es POST tweetValue '
{
  "valueFromMemberId": "2",
  "valueToMemberId": "4",
  "valueToTweetId": "5",
  "valueScore": -1
}'

es POST tweetValue '
{
  "valueFromMemberId": "3",
  "valueToMemberId": "4",
  "valueToTweetId": "5",
  "valueScore": -1
}'
