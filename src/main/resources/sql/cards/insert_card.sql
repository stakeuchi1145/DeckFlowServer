INSERT INTO cards(name, number, card_type_id, pack_id, rarity, image_url, regulation_mark_id, created_by_user_id)
SELECT
    :name,
    :number,
    (SELECT id FROM card_types WHERE name = :card_type_name),
    (SELECT id FROM packs WHERE code = :pack_code),
    :rarity,
    :image_url,
    (SELECT id FROM regulation_marks WHERE code = :regulation_mark_id),
    (SELECT id FROM users WHERE auth_uid = :uid);
