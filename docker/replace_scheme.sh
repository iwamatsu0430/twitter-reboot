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
      "screenName": {
        "index": "not_analyzed",
        "type": "string"
      },
      "displayName": {
        "index": "not_analyzed",
        "type": "string"
      },
      "mail": {
        "index": "not_analyzed",
        "type": "string"
      },
      "password": {"type": "string"},
      "confirm": {
        "type": "boolean",
        "default": false
      },
      "profile": {
        "type": "nested",
        "properties": {
          "iconId": {
            "index": "not_analyzed",
            "type": "string",
            "default": "1"
          },
          "biography": {
            "type": "string",
            "default": ""
          }
        }
      }
    }
  }
}'

es PUT _mapping/follow '
{
  "follow": {
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "followFromId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "followToId": {
        "index": "not_analyzed",
        "type": "string"
      }
    }
  }
}'

es PUT _mapping/withdrawal '
{
  "withdrawal": {
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
      "text": {"type": "string"},
      "replyToId": {
        "index": "not_analyzed",
        "type": "string"
      },
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

es PUT _mapping/favorite '
{
  "favorite": {
    "_timestamp": {
      "enabled": true,
      "store": "yes"
    },
    "properties": {
      "memberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "tweetId": {
        "index": "not_analyzed",
        "type": "string"
      }
    }
  }
}'

es PUT _mapping/hash '
{
  "hash": {
    "properties": {
      "memberId": {
        "index": "not_analyzed",
        "type": "string"
      },
      "hash": {"type": "string"},
      "used": {
        "type": "boolean",
        "default": false
      }
    }
  }
}'

es PUT _mapping/image '
{
  "image": {
    "properties": {
      "path": {"type": "string"}
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
  "screenName": "bison",
  "displayName": "Bison",
  "mail": "bison@streetfighter.org",
  "password": "18b962f50525d6b098409574c2392e7d458b1a9d1cf67759d4d1ddb075cc8177b33a9b215b335ab0b0414371c1b4c3d2033fb709fce6ac38fd90b94107165d7c",
  "confirm": true,
  "profile": {
    "biography": "ボクシング選手です"
  }
}'

# password: Barlog
es POST member/2 '
{
  "screenName": "barlog",
  "displayName": "Barlog",
  "mail": "barlog@streetfighter.org",
  "password": "212324398bf0068f6e4a927cdc3951ba14992099979ef36e784a416119c323cef67a461fcdabc38b3d54d0f2db0a6362261ea74d08d1476358d5d84a31f07241",
  "confirm": true,
  "profile": {
    "biography": "仮面半裸です"
  }
}'

# password: Sagat
es POST member/3 '
{
  "screenName": "sagat",
  "displayName": "Sagat",
  "mail": "sagat@streetfighter.org",
  "password": "ef135173d9666b180e6bd50a74784486e2fd536eb829188b95ebf16f7572cf7811ce77c782be647fbc0da7d103f660ca6bcbdb8f4652da37a9cb1910be3457c9",
  "confirm": true,
  "profile": {
    "biography": "ムエタイです"
  }
}'

# password: Vega
es POST member/4 '
{
  "screenName": "vega",
  "displayName": "Vega",
  "mail": "vega@streetfighter.org",
  "password": "bab45847e2227d0c57136825dc31ba89c450bb522f62864f3d6caee24ef36962a2f89772161335252473da18b5828bfb9f286e2a618e49e0dac1d16f6ba24904",
  "confirm": true,
  "profile": {
    "biography": "サイコパワーが使えます"
  }
}'

# Insert Follow Data
es POST follow '
{
  "followFromId": "1",
  "followToId": "2"
}'

es POST follow '
{
  "followFromId": "1",
  "followToId": "3"
}'

es POST follow '
{
  "followFromId": "2",
  "followToId": "3"
}'

es POST follow '
{
  "followFromId": "4",
  "followToId": "1"
}'

# Insert Withdrawal Data
es POST withdrawal '
{
  "memberId": "4",
  "reason": "飽きた"
}'

# Insert Tweet Data
es POST tweet/1 '
{
  "memberId": "1",
  "text": "hello, world!",
  "deleted": false
}'

es POST tweet/2 '
{
  "memberId": "1",
  "text": "こんにちは世界",
  "deleted": true
}'

es POST tweet/3 '
{
  "memberId": "2",
  "reTweetFromId": "1",
  "deleted": false
}'

es POST tweet/4 '
{
  "memberId": "3",
  "text": "@Bison テスト",
  "replyToId": "1",
  "deleted": false
}'

es POST tweet/5 '
{
  "memberId": "4",
  "text": "twitterやめます",
  "deleted": false
}'

es POST tweet/6 '
{
  "memberId": "2",
  "text": "@Vega ウケる",
  "reTweetFromId": "5",
  "deleted": false
}'

es POST tweet/7 '
{
  "memberId": "2",
  "reTweetFromId": "2",
  "deleted": false
}'

for i in `seq 8 100`
do
  es POST tweet/$i '
  {
    "memberId": "1",
    "text": "連投テスト",
    "deleted": false
  }'
done

es POST favorite '
{
  "memberId": "1",
  "tweetId": "3"
}'

es POST favorite '
{
  "memberId": "2",
  "tweetId": "1"
}'

es POST favorite '
{
  "memberId": "3",
  "tweetId": "1"
}'

es POST favorite '
{
  "memberId": "4",
  "tweetId": "1"
}'

es POST favorite '
{
  "memberId": "2",
  "tweetId": "2"
}'

es POST favorite '
{
  "memberId": "3",
  "tweetId": "5"
}'

es POST image/1 '
{
  "path": "default.png"
}'

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
