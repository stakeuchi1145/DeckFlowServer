# DeckFlowServer API仕様書

## サーバー概要
- ベースURL（デフォルト）: `http://0.0.0.0:8080`
- 返却形式: JSON（`/` のみプレーンテキスト）
- 文字コード: UTF-8
- 認証: Firebase ID トークンによる Bearer 認証
  - `Authorization: Bearer <Firebase ID Token>`
  - 失敗時は 401 として `"Token is missing or invalid"` を返却
- JSON ボディの Content-Type: `application/json`
- ファイルアップロードを伴うエンドポイントは `multipart/form-data` を使用

## エンドポイント一覧
| メソッド | パス        | 認証 | 説明 |
| --- | --- | --- | --- |
| GET | `/` | 不要 | 疎通確認用の Hello World 応答 |
| GET | `/me` | 必須 | 自分のユーザー情報を取得 |
| GET | `/me/cards` | 必須 | 自分の所持カード一覧を取得 |
| POST | `/me/card` | 必須 | 自分の所持カードを登録 |
| GET | `/cards` | 必須 | カード一覧を取得 |
| POST | `/card` | 必須 | カードを登録（画像任意添付可） |
| GET | `/packs` | 必須 | パック一覧を取得 |
| POST | `/pack` | 必須 | パックを登録（画像任意添付可） |

---

## 詳細仕様

### GET `/`
- 認証不要。
- レスポンス: プレーンテキスト `"Hello World!"`

### GET `/me`
- 認証: 必須。
- 正常系レスポンス: HTTP 200
  ```json
  {
    "displayName": "string",
    "email": "string",
    "createdAt": "yyyy-MM-dd HH:mm:ss",
    "updatedAt": "yyyy-MM-dd HH:mm:ss"
  }
  ```
- 異常系: 認証失敗時は 401 `"Token is missing or invalid"`

### GET `/me/cards`
- 認証: 必須。
- 正常系レスポンス: HTTP 200
  ```json
  {
    "myCards": [
      {
        "id": 1,
        "cardName": "string",
        "imageURL": "string",
        "packName": "string",
        "quantity": 2
      }
    ]
  }
  ```
- 異常系: 認証失敗時は 401。

### POST `/me/card`
- 認証: 必須。
- Content-Type: `application/json`
- リクエストボディ
  ```json
  {
    "cardName": "string",
    "packCode": "string",
    "cardNumber": "string",
    "quantity": 1,
    "location": "string"
  }
  ```
  - すべて必須。`quantity` は整数。
- 正常系レスポンス: HTTP 200 `"Card registered successfully"`
  - バリデーション失敗時は 400（理由文字列）。
  - DB 登録失敗時は 500 `"Failed to register card"`。
- 異常系: 認証失敗時は 401。
- サンプル cURL
  ```bash
  curl -X POST "http://0.0.0.0:8080/me/card" \\
    -H "Authorization: Bearer <TOKEN>" \\
    -H "Content-Type: application/json" \\
    -d '{
      "cardName": "ピカチュウ",
      "packCode": "sv01",
      "cardNumber": "001/078",
      "quantity": 1,
      "location": "binder"
    }'
  ```

### GET `/cards`
- 認証: 必須。
- 正常系レスポンス: HTTP 200
  ```json
  {
    "cards": [
      {
        "id": 1,
        "name": "string",
        "number": "string",
        "cardType": "string",
        "packName": "string",
        "rarity": "string",
        "imageUrl": "string",
        "regulationMark": "string"
      }
    ]
  }
  ```
- 異常系: 認証失敗時は 401。

### POST `/card`
- 認証: 必須。
- Content-Type: `multipart/form-data`
- パート構成
  - `data`: JSON テキスト（`CardRequest`）
    ```json
    {
      "name": "string",
      "number": "string",
      "cardType": "string",
      "packCode": "string",
      "rarity": "string",
      "regulationMarkCode": "string",
      "fileName": "string" // 画像を送る場合のみ必須
    }
    ```
  - ファイルパート: 任意（画像）。`fileName` が空でない場合に送信。Content-Type は画像の MIME を設定。
- 正常系レスポンス: HTTP 200
  ```json
  { "result": true }
  ```
  - バリデーションエラー時は 400（理由文字列）。
  - 画像アップロード失敗などサーバー内部エラー時は 500。
- 備考: 画像を送った場合は `card-images/{packCode}/{fileName}` に保存されるパスがカードの `imageUrl` として登録されます。
- サンプル cURL
  ```bash
  curl -X POST "http://0.0.0.0:8080/card" \\
    -H "Authorization: Bearer <TOKEN>" \\
    -F "data={\"name\":\"ピカチュウ\",\"number\":\"001/078\",\"cardType\":\"Poke\",\"packCode\":\"sv01\",\"rarity\":\"C\",\"regulationMarkCode\":\"G\",\"fileName\":\"pika.jpg\"};type=application/json" \\
    -F "file=@./pika.jpg;type=image/jpeg"
  ```

### GET `/packs`
- 認証: 必須。
- 正常系レスポンス: HTTP 200
  ```json
  {
    "packs": [
      {
        "id": 1,
        "name": "string",
        "code": "string",
        "totalCards": 100,
        "releaseDate": "yyyy-MM-dd",
        "imageUrl": "string"
      }
    ]
  }
  ```
- 異常系: 認証失敗時は 401。

### POST `/pack`
- 認証: 必須。
- Content-Type: `multipart/form-data`
- パート構成
  - `data`: JSON テキスト（`PackRequest`）
    ```json
    {
      "name": "string",
      "code": "string",
      "totalCards": 100,
      "releaseDate": "yyyy-MM-dd",
      "fileName": "string" // 画像を送る場合のみ必須
    }
    ```
  - ファイルパート: 任意（画像）。`fileName` が空でない場合に送信。Content-Type は画像の MIME を設定。
- 正常系レスポンス: HTTP 200
  ```json
  { "result": true }
  ```
  - バリデーションエラー時は 400（理由文字列）。
  - 画像アップロード失敗などサーバー内部エラー時は 500。
- 備考: 画像を送った場合は `pack-images/images/{fileName}` に保存されるパスがパックの `imageUrl` として登録されます。
- サンプル cURL
  ```bash
  curl -X POST "http://0.0.0.0:8080/pack" \\
    -H "Authorization: Bearer <TOKEN>" \\
    -F "data={\"name\":\"スカーレット\",\"code\":\"sv01\",\"totalCards\":\"78\",\"releaseDate\":\"2023-01-20\",\"fileName\":\"pack.jpg\"};type=application/json" \\
    -F "file=@./pack.jpg;type=image/jpeg"
  ```

## エラー共通メッセージ
- `401 Unauthorized`: `"Token is missing or invalid"`
- `400 Bad Request`: 必須項目不足やバリデーションエラーの場合に理由文字列を返却。
- `500 Internal Server Error`: 登録やアップロード処理の失敗時に返却。
