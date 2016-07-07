-- Copyright 2016 Google Inc. All Rights Reserved.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

{-# LANGUAGE OverloadedStrings #-}

module Main where

import qualified Data.Text as T
import qualified Data.Map.Strict as M
import Data.List (delete)
import Control.Monad.Plus

data Color = White | Red | Blue
  deriving (Eq, Ord, Show)

data Piece = Piece !T.Text !Color !Color !Color !Color
  deriving (Eq, Ord, Show)

data Coord = Coord !Int !Int
  deriving (Eq, Ord, Show)

newtype Board = Board (M.Map Coord Piece)
  deriving Show

rotations (Piece n a b c d) =
  [ Piece n a b c d
  , Piece n b c d a
  , Piece n c d a b
  , Piece n d a b c
  ]

neighbors :: Board -> Coord -> (Maybe Piece, Maybe Piece)
neighbors (Board b) (Coord i j) = (M.lookup (Coord (i-1) j) b, M.lookup (Coord i (j-1)) b)

insert :: Coord -> Piece -> Board -> Board
insert coord piece (Board b) = Board (M.insert coord piece b)

fits Nothing Nothing _ = True
fits (Just (Piece _ _ _ c _)) Nothing (Piece _ a _ _ _) = a == c
fits Nothing (Just (Piece _ _ b _ _)) (Piece _ _ _ _ d) = d == b
fits (Just (Piece _ _ _ c _)) (Just (Piece _ _ b _ _)) (Piece _ a _ _ d) = a == c && d == b

zigZag numRows numCols = [Coord i j | i <- [0..numRows-1], j <- [0..numCols-1]]

solve' :: MonadPlus m => [Coord] -> [Piece] -> Board -> m Board
solve' [] _ board = return board
solve' (coord:coords) pieces board = do
  let (top, left) = neighbors board coord
  piece <- mfromList pieces
  piece' <- mfromList (rotations piece)
  if fits top left piece'
    then solve' coords (delete piece pieces) (insert coord piece' board)
    else mzero

solve :: MonadPlus m => Int -> Int -> [Piece] -> m Board
solve numRows numCols pieces = solve' (zigZag numRows numCols) pieces (Board M.empty)

example =
  [ Piece "A" Red White White White
  , Piece "B" Blue White White White
  , Piece "C" Blue Red Red Red
  , Piece "D" White Red Blue Blue
  , Piece "E" Blue Red White Red
  , Piece "F" Blue Blue Blue Blue
  , Piece "G" Red White White Red
  , Piece "H" Blue White White Blue
  , Piece "I" Blue Red Red Blue
  , Piece "J" Red White Blue Blue
  , Piece "K" Red Blue White Blue
  , Piece "L" Red Red Red Red
  , Piece "M" Red White Red Red
  , Piece "N" Blue White Blue Blue
  , Piece "O" Blue Red Blue Blue
  , Piece "P" Blue White Red Red
  , Piece "Q" White White White White
  , Piece "R" White White Blue Red
  , Piece "S" White Red White Red
  , Piece "T" White Blue White Blue
  , Piece "U" Red Blue Red Blue
  , Piece "V" White Blue Red Red 
  , Piece "W" White Red White Blue
  , Piece "X" White White Red Blue]

main :: IO ()
main = mapM_ print (solve 6 4 example :: [Board])
