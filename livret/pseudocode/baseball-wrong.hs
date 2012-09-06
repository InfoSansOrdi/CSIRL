import Data.List (nub,permutations,maximumBy)
import qualified Data.Array.Unboxed as A

type Joueur = Int
type Base = Int
type Etat = A.Array Joueur Base

-- nombre de bases, nombres de places par base, etat
data Jeu = Jeu Int Int Etat
  deriving (Show,Eq)

-- là où le joueur doit finir
baseDuJoueur :: Joueur -> Jeu -> Int
baseDuJoueur joueur (Jeu _ p _) = joueur `div` p

-- là où le joueur se trouve
positionDuJoueur :: Joueur -> Jeu -> Int
positionDuJoueur joueur (Jeu b p e) =
 e A.! joueur 

-- les joueurs présents sur une base
joueursSurLaBase :: Int -> Jeu -> [Joueur]
joueursSurLaBase base (Jeu _ _ etat) =
  filtre $ A.assocs etat
  where filtre [] = []
        filtre ((j,b):suite) = if b == base
          then j : filtre suite
          else filtre suite

distanceAParcourir :: Joueur -> Jeu -> Int
distanceAParcourir joueur jeu@(Jeu b _ _) = 
  if from >= to
  then from - to
  else b + (from - to)
  where from  = positionDuJoueur joueur jeu
        to    = baseDuJoueur joueur jeu

deplacerJoueur :: Joueur -> Jeu -> Jeu
deplacerJoueur joueur jeu@(Jeu b p e) =
  Jeu b p e'
  where e'    = e A.// [(joueur, to), (0, from)]
        from  = e A.! joueur
        to    = e A.! 0

bienPlace :: Joueur -> Jeu -> Bool
bienPlace joueur jeu = 
  positionDuJoueur joueur jeu == baseDuJoueur joueur jeu

victoire :: Jeu -> Bool
victoire jeu@(Jeu b p _)=
  and [bienPlace j jeu | j <- js]
  where js = [0..b*p-1]

etatsInitiaux :: Int -> Int -> [Jeu]
etatsInitiaux bases places =
  [Jeu bases places (A.array bornes p) | p <- paires]
  where bornes    = (0,bases*places-1)
        joueurs   = [0..bases*places-1]
        positions = [j `div` places | j <- joueurs]
        paires = [zip joueurs pos | pos <- nub $ permutations positions]



-- premier algo

baseLibre jeu = 
  positionDuJoueur 0 jeu

baseSuivante jeu@(Jeu b _ _) =
  (baseLibre jeu + 1) `mod` b

joueursDeplacables jeu =
  joueursSurLaBase (baseSuivante jeu) jeu

joueurLePlusLointain :: [Joueur] -> Jeu -> Joueur
joueurLePlusLointain joueurs jeu =
  maximumBy comp joueurs
  where comp a b = compare (distanceAParcourir a jeu) (distanceAParcourir b jeu)


algo :: [Jeu] -> Jeu
algo [] = error "au moins un élément dans la liste"
algo hist@(h:hs) = 
  if victoire h || h `elem` hs
  then h
  else algo (suivant:hist)
  where suivant = deplacerJoueur j h
        j = joueurLePlusLointain jd h
        jd = joueursDeplacables h

-- test

proportionBoucles vs =
  boucles / fromIntegral (length vs)
  where boucles = foldr (\j score -> if not j then score+1 else score) 0 vs

jeux32 = etatsInitiaux 3 2
solutions32 = map (\j -> algo [j]) jeux32
victoires32 = map victoire solutions32
proportion32 = proportionBoucles victoires32

jeux42 = etatsInitiaux 4 2
solutions42 = map (\j -> algo [j]) jeux42
victoires42 = map victoire solutions42
proportion42 = proportionBoucles victoires42

jeux52 = etatsInitiaux 5 2
solutions52 = map (\j -> algo [j]) jeux52
victoires52 = map victoire solutions52
proportion52 = proportionBoucles victoires52

main = do
  print proportion52

