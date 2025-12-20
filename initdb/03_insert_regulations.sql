-- ========================================================
-- 1. regulations : レギュレーション
-- ========================================================
INSERT INTO regulations (code, name, description, valid_from)
VALUES
    ('STANDARD', 'スタンダード', '現行レギュレーション', '2024-01-01'),
    ('EXPANDED', 'エクストラ', '旧カードを含む拡張ルール', '2020-01-01')
    ON CONFLICT (code) DO NOTHING;


-- ========================================================
-- 2. regulation_marks : レギュマーク (G, H)
-- ========================================================
INSERT INTO regulation_marks (code, name)
VALUES
    ('G', 'レギュレーションマーク G'),
    ('H', 'レギュレーションマーク H'),
    ('I', 'レギュレーションマーク I');

-- STANDARD が G/H を許可
INSERT INTO regulation_allowed_marks (regulation_id, regulation_mark_id)
SELECT r.id, m.id
FROM regulations r, regulation_marks m
WHERE r.code = 'STANDARD'
  AND m.code IN ('G', 'H', 'I');


-- ========================================================
-- 3. packs : パック
-- ========================================================
INSERT INTO packs (name, code, total_cards, release_date, image_url)
VALUES
    ('スカーレットex', 'sv1S', 78, '2024-01-20',
     ''
    ),
    ('超電ブレイカー', 'sv8', 106, '2024-10-18',
     ''
    ),
    ('MEGAドリームex', 'm2a', 193, '2025-11-28',
     ''
    );


-- ========================================================
-- 4. cards : カードマスタ
-- 画像URLは MinIO の card-images/{pack}/{no}.webp に統一
-- ========================================================

-- コライドンex
INSERT INTO cards (name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    'コライドンex', '050/078',
    (SELECT id FROM card_types WHERE code = 'POKEMON'),
    (SELECT id FROM packs WHERE code = 'sv1S'),
    'RR',
    'card-images/sv1S/050.jpg',
    (SELECT id FROM regulation_marks WHERE code = 'G'),
    (SELECT id FROM users WHERE email = 'taro@example.com')
    WHERE NOT EXISTS (
    SELECT 1 FROM cards
    WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv1S')
      AND number = '050/078'
);

-- ピカチュウex
INSERT INTO cards (name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    'ピカチュウex', '033/106',
    (SELECT id FROM card_types WHERE code = 'POKEMON'),
    (SELECT id FROM packs WHERE code = 'sv8'),
    'RR',
    'card-images/sv8/033.jpg',
    (SELECT id FROM regulation_marks WHERE code = 'G'),
    (SELECT id FROM users WHERE email = 'taro@example.com')
    WHERE NOT EXISTS (
    SELECT 1 FROM cards
    WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv8')
      AND number = '033/106'
);

-- ピカチュウex
INSERT INTO cards (name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    'ピカチュウex', '122/106',
    (SELECT id FROM card_types WHERE code = 'POKEMON'),
    (SELECT id FROM packs WHERE code = 'sv8'),
    'SR',
    'card-images/sv8/122.jpg',
    (SELECT id FROM regulation_marks WHERE code = 'G'),
    (SELECT id FROM users WHERE email = 'taro@example.com')
    WHERE NOT EXISTS (
    SELECT 1 FROM cards
    WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv8')
      AND number = '122/106'
);

-- ヒビキのホウオウex
INSERT INTO cards (name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    'ヒビキのホウオウex', '021/193',
    (SELECT id FROM card_types WHERE code = 'POKEMON'),
    (SELECT id FROM packs WHERE code = 'm2a'),
    'RR',
    'card-images/m2a/021.jpg',
    (SELECT id FROM regulation_marks WHERE code = 'G'),
    (SELECT id FROM users WHERE email = 'taro@example.com')
    WHERE NOT EXISTS (
    SELECT 1 FROM cards
    WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a')
      AND number = '021/193'
);

-- ピカチュウex
INSERT INTO cards (name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    'ピカチュウex', '044/193',
    (SELECT id FROM card_types WHERE code = 'POKEMON'),
    (SELECT id FROM packs WHERE code = 'm2a'),
    'RR',
    'card-images/m2a/044.jpg',
    (SELECT id FROM regulation_marks WHERE code = 'G'),
    (SELECT id FROM users WHERE email = 'taro@example.com')
    WHERE NOT EXISTS (
    SELECT 1 FROM cards
    WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a')
      AND number = '044/193'
);


-- ========================================================
-- 5. user_cards : 所持カード
-- ========================================================

-- コライドンex
INSERT INTO user_cards (user_id, card_id, quantity, location)
SELECT
    (SELECT id FROM users WHERE email = 'taro@example.com'),
    (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv1S') AND number = '050/078'),
    2,
    '自宅'
    WHERE NOT EXISTS (
    SELECT 1 FROM user_cards
    WHERE card_id = (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv1S') AND number = '050/078')
      AND user_id = (SELECT id FROM users WHERE email = 'taro@example.com')
    );

-- ピカチュウex
INSERT INTO user_cards (user_id, card_id, quantity, location)
SELECT
    (SELECT id FROM users WHERE email = 'taro@example.com'),
    (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv8') AND number = '122/106'),
    1,
    '自宅'
    WHERE NOT EXISTS (
    SELECT 1 FROM user_cards
    WHERE card_id = (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'sv8') AND number = '122/106')
      AND user_id = (SELECT id FROM users WHERE email = 'taro@example.com')
    );

-- ヒビキのホウオウex
INSERT INTO user_cards (user_id, card_id, quantity, location)
SELECT
    (SELECT id FROM users WHERE email = 'taro@example.com'),
    (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a') AND number = '021/193'),
    4,
    '自宅'
    WHERE NOT EXISTS (
    SELECT 1 FROM user_cards
    WHERE card_id = (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a') AND number = '021/193')
      AND user_id = (SELECT id FROM users WHERE email = 'taro@example.com')
    );

-- ピカチュウex
INSERT INTO user_cards (user_id, card_id, quantity, location)
SELECT
    (SELECT id FROM users WHERE email = 'taro@example.com'),
    (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a') AND number = '044/193'),
    8,
    '自宅'
    WHERE NOT EXISTS (
    SELECT 1 FROM user_cards
    WHERE card_id = (SELECT id FROM cards WHERE pack_id = (SELECT id FROM packs WHERE code = 'm2a') AND number = '044/193')
      AND user_id = (SELECT id FROM users WHERE email = 'taro@example.com')
    );
