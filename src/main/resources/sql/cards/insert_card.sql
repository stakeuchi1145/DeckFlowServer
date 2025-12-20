INSERT INTO cards(name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    ?,
    ?,
    (SELECT id FROM card_types WHERE name = ?),
    (SELECT id FROM packs WHERE code = ?),
    ?,
    ?,
    (SELECT id FROM regulation_marks WHERE code = ?),
    (SELECT id FROM users WHERE auth_uid = ?)
RETURNING id;
