SELECT
    c.id as card_id,
    c.name as card_name,
    c.number as card_number,
    ct.name as card_type,
    p.name as pack_name,
    c.image_url as image_url,
    rm.name as regulation_mark,
    c.rarity as rarity
FROM cards as c
         LEFT JOIN packs as p
                   ON c.pack_id = p.id
         LEFT JOIN card_types as ct
                   ON c.card_type_id = ct.id
         LEFT JOIN regulation_marks as rm
                   ON c.regulation_mark_id = rm.id;