# 必要なもの

- docker-compose
- Scala
- sbt
- node

# 実行手順

1. dockerの起動
  ```
  $ cd docker
  $ docker-compose create # 初回のみ
  $ docker-compose start
  ```

2. バックエンドサーバの起動
  ```
  $ cd backend
  $ sbt run
  ```

3. フロントエンドサーバの起動
  ```
  $ cd frontend
  $ npm i
  $ npm run dev
  ```

4. [http://localhost:8080/](http://localhost:8080/)へアクセス
