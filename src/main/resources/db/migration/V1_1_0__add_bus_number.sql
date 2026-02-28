-- Ajouter la colonne en autorisant NULL temporairement
ALTER TABLE buses ADD COLUMN bus_number VARCHAR(50);

-- Donner un numero provisoire aux bus existants
-- pour pouvoir passer la colonne en NOT NULL ensuite
UPDATE buses SET bus_number = 'BUS-' || UPPER(SUBSTRING(CAST(id AS VARCHAR), 1, 6))
WHERE bus_number IS NULL;

-- Passer la colonne en NOT NULL maintenant que toutes les lignes ont une valeur
ALTER TABLE buses ALTER COLUMN bus_number SET NOT NULL;

-- Ajouter la contrainte d'unicite
ALTER TABLE buses ADD CONSTRAINT buses_bus_number_unique UNIQUE (bus_number);

-- Verifier le resultat
SELECT id, bus_number, model, status FROM buses;