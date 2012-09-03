#!/usr/bin/env python
from collections import namedtuple, Counter
from itertools import permutations as perms

# etat: [(couleur base, couleur pion)]
done = {} # -1: loop, 0: in processing, 1: finish
def compute(nb_places, nb_bases, etat):
  if (etat in done):
    if done[etat]==1:
      return 1
    else:
      return -1
  baselibre = None
  for b, c in etat:
    if c==-1:
      baselibre = (b,c)
      break
  assert baselibre is not None, 'No empty base'

  newbaselibre = ((baselibre[0]+1)%nb_bases, -1)
  joueurdep = [(b,c) for b, c in etat if b==newbaselibre[0]]
  assert len(joueurdep)==nb_places, 'Not enough players in base %s (%s), %s'%(newbaselibre[0], joueurdep, etat)

  joueurloin = max(joueurdep, key=lambda x: x[1]-x[0] if x[0]<=x[1]
                                                   else x[1]-x[0]+nb_bases-1)
  netat = list(etat)
  netat.remove(baselibre)
  netat.remove(joueurloin)
  netat.append(newbaselibre)
  netat.append((baselibre[0], joueurloin[1]))
  netat.sort()
  netat = tuple(netat)
  if (all(b==c for b,c in etat if c!=-1)):
    done[etat] = 1
    return 1
  done[etat] = 0
  res = compute(nb_places, nb_bases, netat)
  done[etat] = res
  return res

if __name__=='__main__':
  nb_bases = 5
  nb_places = 2
  all_states = set(tuple(sorted((i%nb_bases, p) 
                              for i, p in zip(range(nb_places*nb_bases),
                                              perm)))
                       for perm in perms(range(-1,nb_bases-1) +
                             range(nb_bases)*(nb_places-1)))
#  print compute(nb_places, nb_bases, etat)
  
  # compute the outcome of all states
  for e in all_states:
      compute(nb_places, nb_bases, e)
      
  # Prepare the filtering
  initial_states = all_states 
  # filter out all states where someone is already home: we never take such states as initial states
  initial_states = filter(lambda x: all(b!=p for b,p in x), initial_states)
  # filter out all states where 2 guys of the same color are at the same base: we never take such states as initial states  
  initial_states = filter(lambda e: filter(lambda x: x[1]==2 and x[0]!=x[1], Counter(e).items()), initial_states)

  res = Counter(done[e]
                for e in initial_states)
  outcome={}
  outcome[-1]="looping"
  outcome[1]="OK"
  print '\n'.join( str(e)+": "+str(outcome[done[e]]) for e in initial_states)
              
  print "Loop:",res[-1],", No loop:",res[1]," => Looping in ",( float(res[-1])/(res[-1]+res[1])*100),"% of cases"
