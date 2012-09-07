package main

import "fmt" 
import "runtime"
import "time"

const nb_base = 5
const print_states_looping = false
const print_states_ok = false
const print_timings = true

const filter_home = true


type state []int

var start = time.Now();
func now() (string) {
  return fmt.Sprintf("%v",time.Now().Sub(start));
}


// sort: modifies the given state to sort its elements
// ie, within each base, ensure that the left elm is smaller than the right one
func (e state) sort() {
  for i:=0;i<nb_base;i++ {
    rank:=i*2;
    if e[rank] > e[rank+1] {
      e[rank], e[rank+1] = e[rank+1], e[rank];
    }
  }
}

func (e state) is_sorted(until ...int) (bool) {
	if len(e) != nb_base*2 {
		fmt.Println("state cannot be sorted, its len is ",len(e),"instead of ",nb_base*2);
		return false;
	}
		  
  for i:=0;i<nb_base;i++ {
    rank:=i*2;
    if e[rank] > e[rank+1] {
      return false;
    }
  }
  return true;
}

func (e state) is_initial() (bool) {
  if !e.is_sorted() {
    return false;
  }
  
  if filter_home { // filter out situations where one dude is already home
    for i:=0;i<nb_base;i++ {
      if e[i*2] == i || e[i*2+1] == i {
        return false;
      }
    }
  }
  
  return true;
}

func (e state) is_final() (bool) {
  for i:=0;i<nb_base-1;i++ {
    rank:=i*2;
    if (e[rank] != i) || (e[rank+1] != i) {
      return false;
    }
  }
  // Don't test the last base: N and -1 were not on the other ones, they must be there.
  return true;
}

func (e state) equals(other state) (bool) {
  for i:=0;i<2*nb_base;i++ {
    if (e[i] != other[i]) {
      return false;
    }
  }
  return true;
}

func (e state) toString() (string) {
  return fmt.Sprintf("%v",e);
}

func (e state) move() {
  // search the empty spot. It's on the left of a base
  var empty_base int;
  for i:=0;i<nb_base;i++ {
    if e[2*i] == -1 {
      empty_base = i;
      break;
    }
  }
  new_empty_base := (empty_base+1) % nb_base;
  
  var cand = []int{new_empty_base*2, new_empty_base*2+1};
  var dist = make([]int, 2);
  
  for i:=0;i<2;i++ {
    if cand[i]/2 >= e[cand[i]] {
      dist[i] = (cand[i]/2) - e[cand[i]];
    } else {
      dist[i] = (cand[i]/2) - e[cand[i]] + nb_base;
    }
  }
  
//  fmt.Print("slot: ",empty_base,
//            "; c1: ",e[cand[0]]," d: ",dist[0],
//            ";   c2: ",e[cand[1]]," d: ",dist[1],"  ");

  if dist[0]>dist[1] {
    e[empty_base*2] = e[cand[0]]; // remember that -1 is on the left of the base
    e[cand[0]] = -1;
  } else {
    e[empty_base*2] = e[cand[1]];
    e[cand[1]] = -1;
  }
  e.sort();
}

// Warning: it modifies the call receiver
func (e state) is_looping(memo map[string]bool) (bool) { 
  str := e.toString();
  var val,ok = memo[str];
  if ok { // we were there already
    return val;
  }
  if e.is_final() {
    memo[str] = false;
    return false;
  }
  memo[str] = true; // if we move there again, we are looping
  e.move();
  if !e.is_looping(memo) {
    memo[str] = false;
  }
  return memo[str];
}


////////////////////////////////////////////////////////////////////
/// The generator of permutations.
// it takes the first position of its solutions as an argument, 
// computes the permutations of the other positions, and 
// accumulates the amount of looping/terminating permutations it sees
////////////////////////////////////////////////////////////////////
func generate(position int, result chan handler) {
  var a state;
  a = make([]int, nb_base*2);
  var acc handler;
  acc.init(position+1);
  
  if print_timings {
    fmt.Printf("[%s] Generator %d starting\n",now(), position+1);
  }

  // build the array to permute (must be in lexicographic order)
  to_permute := make([]int, nb_base*2);
  for i:=0;i<nb_base;i++ {
    to_permute[2*i] = i - 1
    to_permute[2*i+1] = i
  }
  
  // build the first version of the array, before launching the permutations
  // It's like to_permute, but a[0] == position-1, and position-1
  //   is removed from where it were later in the array.
  first := position-1
  a[0]=first
  for i,from:=1,0;i<nb_base*2;from++ {
    if to_permute[from] == first {
      first = -42; // never ignore any other elements
    } else {
      a[i] = to_permute[from]
      i++
    }
  }
  //fmt.Println(position,": ",to_permute,"->",a);
  
  // the first permutation is ready to use
  acc.consume(a)

  // Let's generate the permutations (following http://en.wikipedia.org/wiki/Permutation#Generation_in_lexicographic_order)
  for {    
    // Find the largest index k such that a[k] < a[k + 1].
    k,l := -1,1;
    for i:=1;i<nb_base*2 -1;i++ {
      if a[i]<a[i+1] {
        k = i;
      } 
      // Find the largest index l such that a[k] < a[l] (exists in all cases).
      if k>0 && a[k]<a[i] {
         l = i;
      }
    }

    // If no such index k exists, the permutation is the last permutation.
    if k == -1 {
      result <- acc;
      if print_timings {
        fmt.Printf("[%v] Generator %d terminating (handled %d permutations)\n",
	  now(),position+1,acc.terminating+acc.looping);
      }
      return;
    }

    // compute l also when k is the last possible value
    if a[k]<a[nb_base*2 -1] { l = nb_base*2 -1; }
    
    // Swap a[k] with a[l] -- go powa!
    a[k],a[l] = a[l], a[k]
    
    // Reverse the sequence from a[k + 1] up to and including the final element a[n].
    n := nb_base*2
    for j:=1; k+j<n-j ; j++ {
      tmp := a[k+j];
      a[k+j] = a[n-j];
      a[n-j] = tmp;
    }
     
    // the new permutation is ready to use. 
    acc.consume(a);
  }
}


///////////////////////////////////////////////////////////////////////////////
// The consumer logic
//
// Takes permutation passed to consume function, and checks whether it terminates
// or loops. It use its own memoizer for that, so that we can have
// several consumers
///////////////////////////////////////////////////////////////////////////////
type handler struct { // how consumers say back to main what they found
  rank int;
  terminating, looping int;
  memo map[string]bool;
  data state; // Working area: we modify the state to compute whether it loops,
}

func (h *handler) init(rank int) {
  h.rank = rank;
  h.terminating = 0;
  h.looping = 0;
  h.memo = make(map[string]bool);
  h.data = make([]int, nb_base*2); 
}

func (h *handler) consume(todo state) {
  // If we are passed a state that is not valid as initial state, just ignore it
  if !todo.is_initial() {
    return;
  }

  // copy the state before changing it (with is_looping), so that we can display it afterward
  copy(h.data,todo);
  if h.data.is_looping(h.memo) {
     if print_states_looping { fmt.Println("XXX",todo,": looping"); }
     h.looping++;
  } else {
     if print_states_ok { fmt.Println("XXX",todo,": OK"); }
     h.terminating++;
  }   
  if ((h.looping+h.terminating) %100000 == 0) && print_timings {
     fmt.Printf("[%s] Consumer %d: states seen so far: %d; Looping: %d; Terminating: %d; Looping in %f%% of cases\n",
                now(),h.rank,(h.looping+h.terminating),h.looping,h.terminating,100.0*float64(h.looping)/(float64(h.terminating)+float64(h.looping)));
  }   
}

func test_all() {
  results := make(chan handler, nb_base);

  for i:=0;i<nb_base;i++ {
    if (i!=1 || !filter_home) {
      // no need to compute the permutations
      go generate(i, results);
    } else {
      fmt.Println("Not launching generator 1: it would compute the permutations {0,...} and the nobody@home filter is in use");
    }
  }
   
  // wait for all producers, accumulating their results
  ok,looping := 0,0;
  for i:=0;i<nb_base;i++ {
    if (i!=1 || !filter_home) {
      res := <- results;
      ok+=res.terminating;
      looping+=res.looping;
    }
  }

  fmt.Printf("[%s] Amount of bases: %d; Filter @home: %t; Amount of permutations: %d; Looping: %d; Terminating: %d; Looping in %f%% of cases\n",
             now(),nb_base,filter_home, looping+ok,looping,ok,100.0*float64(looping)/(float64(ok)+float64(looping)));
  
}

func test_one(data []int) {
  var e state;
  if len(data) != nb_base*2 {
    fmt.Printf("len(data)=%d, %d expected for %d bases\n",len(data),nb_base*2,nb_base);
    return;
  }
  e = make([]int,nb_base*2);
  copy(e,data);
  
  memo := make(map[string]bool);
  memo[e.toString()] = true;

  fmt.Println(e);
  for i:=0;i<50;i++ {
    e.move();
    str := e.toString();
    _,ok := memo[str];
    if (e.is_final()) {
      fmt.Println(e,"FINAL!");
      return;
    }
    if ok {
      fmt.Println(e,"already seen");
    } else {
      fmt.Println(e,"new state");
    }
    memo[str] = true;
  }
}

func main() {
  runtime.GOMAXPROCS(runtime.NumCPU());
  fmt.Printf("We have %d cores (to compute %d bases); we use %d of them\n",
             runtime.NumCPU(),nb_base,
             runtime.GOMAXPROCS(-1));
//  runtime.GOMAXPROCS(1); // We want to run in sequential
  test_all();
 
  //  test_one([]int{1,1, 0,2, -1,3, 0,2}); // not looping state, for nb_base=4
  //  test_one([]int{1, 1, -1, 4, 0, 0, 2, 2, 3, 3});  // Looping -- for nb_base=5
}


// Results when filter home is enabled:
// [  XX   10 ms]  #bases: 4; #permutations:      84 (    0 looping, 0%)
// [  XX  111 ms]  #bases: 5; #permutations:    1824 (   24 looping, 1.315789%)
// [  XX    s]  #bases: 6; #permutations:   58860 ( 1251 looping, 2.125382%)
// [2m6      s]  #bases: 7; #permutations: 2633940 (84444 looping, 3.205996%)

