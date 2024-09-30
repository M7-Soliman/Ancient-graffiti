delete from inscriptions where property_id IS NULL and graffiti_id in (select inscriptions.graffiti_id from inscriptions where ancient_city='Pompeii');
DELETE FROM inscriptions WHERE graffiti_id IN (SELECT inscriptions.graffiti_id from inscriptions left join inscriptions on (inscriptions.graffiti_id = inscriptions.graffiti_id) where inscriptions.graffiti_id IS Null);
