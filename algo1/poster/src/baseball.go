package main

import "fmt" 
import "runtime"
import "time"
import "bytes"

const nb_base = 4
const print_states_looping = false
const print_states_ok = false
const print_timings = true

const print_graph = true

const filter_home = false
const filter_double = false

var bases =[]string{"A","B","C","D","E","F","G","H","I","J"};
var maisonNames = []string{"0","1","2","3","Quatre","Cinq","Six"};

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

// Returns true if that state is a valid starting situation according to the rules
func (e state) is_home() (bool) {
  for i:=0;i<nb_base;i++ {
    if e[i*2] == i || e[i*2+1] == i {
      return true
    }
  }
  return false
}
func (e state) is_double() (bool) {
  for i:=0;i<nb_base;i++ {
    if e[i*2] == e[i*2+1] {
      return true
    }
  }
  return false
}

func (e state) is_filtered() (bool) {
  return e.is_home() || e.is_double()
}
// Returns true if that state is a valid starting situation according to the rules
func (e state) is_initial() (bool) {
  if !e.is_sorted() {
    return false;
  }
  
  if filter_home && e.is_home() { // filter out situations where one dude is already home
    return false
  }
  if filter_double && e.is_double() {
    return false
  }
  
  return true;
}

// Returns true if that state is a valid final situation
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

func (e state) name() (string) {
  var buffer bytes.Buffer

  for i:=0;i<2*nb_base;i++ {
    if e[i] == -1 {
      buffer.WriteString("Z")
    } else {
      buffer.WriteString( bases[ e[i] ] );
    }
  }
  return buffer.String()
}

func printBase(e state, pos int, printPos int) (string) {
  if (e[pos] == -1) {
    return fmt.Sprintf("%v/Z", printPos);
  }
  return fmt.Sprintf("%v/%s", printPos, bases[ e[pos] ]);
}
func (e state) tikz() (string) {
  var buffer bytes.Buffer
  
  buffer.WriteString(printBase(e,0,2));buffer.WriteString(",")
  buffer.WriteString(printBase(e,1,3));buffer.WriteString(",")
  
  buffer.WriteString(printBase(e,2,5));buffer.WriteString(",")
  buffer.WriteString(printBase(e,3,6));buffer.WriteString(",")

  buffer.WriteString(printBase(e,4,8));buffer.WriteString(",")
  buffer.WriteString(printBase(e,5,9));buffer.WriteString(",")

  buffer.WriteString(printBase(e,6,11));buffer.WriteString(",")
  buffer.WriteString(printBase(e,7,12))

  if (nb_base>4) {
    buffer.WriteString(",")

    buffer.WriteString(printBase(e,8,14));buffer.WriteString(",")
    buffer.WriteString(printBase(e,9,15))
  }

  return buffer.String()
}

func (e state) move() {
  var nameInit = e.name()

  if (print_graph) {
    if e.is_home() {
      filterStates[e.name()] = 1
    } else {
      nofilterStates[e.name()] = 1
    }
    tikzState[nameInit] = e.tikz()
  }
  
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
  
  if dist[0]>dist[1] {
    e[empty_base*2] = e[cand[0]]; // remember that -1 is on the left of the base
    e[cand[0]] = -1;
  } else {
    e[empty_base*2] = e[cand[1]];
    e[cand[1]] = -1;
  }
  e.sort();
  if (print_graph) { 
    nextState[nameInit] = e.name()
    prevState[e.name()] = nameInit
    tikzState[e.name()] = e.tikz()
  }
}

// Warning: it modifies the call receiver
func (e state) is_looping(memo map[string]bool) (bool) { 
  str := e.name();
  
  var val,ok = memo[str];
  if ok { // we were there already
    //fmt.Println(str,"is in memo:",val)
    return val;
  }
  if e.is_final() {
    if (print_graph) { finalStates[e.name()] = true }
    //fmt.Println(str,"is final")
    memo[str] = false;
    return false;
  } else {
    if (print_graph) { finalStates[e.name()] = false }
  }
  memo[str] = true; // if we move there again, we are looping
  e.move();
  if !e.is_looping(memo) {
    //fmt.Println(str,"->",e.name()," not looping")
    if (nb_base >6) {
      // We're short on memory
      delete(memo,str)
      return false
    } else {
      memo[str] = false;
    }
  }
  //fmt.Println(str,"->",e.name()," looping")
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
  var acc handler;
  acc.init(position+1);
  a=make([]int,nb_base*2)
  
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
// or loops. It useu its own memoizer for that, so that we can have
// several consumers
///////////////////////////////////////////////////////////////////////////////
type handler struct { // how consumers say back to main what they found
  rank int;
  terminating, looping int;
  memo map[string]bool;
  data state; // Working area: we modify the state to compute whether it loops,
}

var nextState = make( map[string]string )
var prevState = make( map[string]string )
var tikzState = make( map[string]string )
var finalStates = make( map[string]bool )
var filterStates = make( map[string]int )
var nofilterStates = make( map[string]int )

var loopStates = make( map[string]bool )
var treeStates = make( map[string]bool )

func (h *handler) init(rank int) {
  h.rank = rank;
  h.terminating = 0;
  h.looping = 0;
  h.memo = make(map[string]bool);
  h.data = make([]int,nb_base*2)
}

func (h *handler) consume(todo state) {
  // If we are passed a state that is not valid as initial state, just ignore it
  if !todo.is_initial() {
    return;
  }

  // copy the state before changing it (with is_looping), so that we can display it afterward
  copy(h.data, todo);
  if h.data.is_looping(h.memo) {
     if print_states_looping && h.data.is_filtered() { 
       fmt.Println("XXX",todo.name(),": looping, wrongly detected as valid");
     }
     h.looping++;
     if print_graph && h.data.is_sorted() {
       treeStates[todo.name()] = false
       loopStates[todo.name()] = true
     }

  } else {
     if print_states_ok { fmt.Println("XXX",todo,": OK"); }
     h.terminating++;
     if print_graph && h.data.is_sorted() {
       treeStates[todo.name()] = true
       loopStates[todo.name()] = false
     }
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

  if (!print_graph) {
    fmt.Printf("[%s] Amount of bases: %d; Filter @home: %t; Amount of permutations: %d; Looping: %d; Terminating: %d; Looping in %f%% of cases\n",
             now(),nb_base,filter_home, looping+ok,looping,ok,100.0*float64(looping)/(float64(ok)+float64(looping)));
  }
  
}

func main() {
  
  //runtime.GOMAXPROCS(runtime.NumCPU())
  runtime.GOMAXPROCS(1); // We want to run in sequential
  
  if (print_graph) {
    runtime.GOMAXPROCS(1); // We want to run in sequential
  } else {
    fmt.Printf("We have %d cores (to compute %d bases); we use %d of them\n",
               runtime.NumCPU(),nb_base,
               runtime.GOMAXPROCS(-1));
  }
  
  test_all();

  if (print_graph) {
  /*
    fmt.Println("\\begin{tikzpicture}[ultra thick,text=white,font=\\tiny]")
    fmt.Println("\\graph [tree layout, coarsen, node distance=24mm] {")
    // Display first all roots to help the layout algorithm
    for f := range finalStates {
      if (finalStates[f]) {
        var e = prevState[f]
        fmt.Println ("  ",f, "[thick,circle,minimum size=20.5mm,draw=white] <- ", e, "[thick,circle,minimum size=20.5mm,draw=white];");
      }
    }
    // Then display all other elements of the tree
    for e := range treeStates { // Every node is in there, but value is true/false
      if (treeStates[e] && !finalStates[e]) { // Final states have no next elements to display
        var f = nextState[e]
        fmt.Println ("  ",f, "[thick,circle,minimum size=20.5mm,draw=white] <- ", e, "[thick,circle,minimum size=20.5mm,draw=white];");
      }
    }
    fmt.Println("};")

    fmt.Println("% Regular (tree) States")
    for k := range nofilterStates {
      if treeStates[k] {
        fmt.Printf("\\node[at=(%s.base),thick,draw=black,circle,minimum size=20mm] {~};\n",k)
      }
    }
    fmt.Println("% (tree) States forbidden by filters")
    for k := range filterStates {
      if treeStates[k] {
        fmt.Printf("\\node[at=(%s.base),thick,draw=red,fill=red!8,circle,minimum size=20mm] {~};\n",k)
      }
    }
    fmt.Println("% All (tree) states")
    for k := range nextState {
      if treeStates[k] {
        fmt.Printf("\\maison%s{%s}{%s};\n",maisonNames[nb_base],k,tikzState[k])
      }
    }
    fmt.Println("% Final states")
    for k := range finalStates {
      if finalStates[k] {
        fmt.Printf("\\node[at=(%s.base),thick,draw=green,fill=green!20,circle,minimum size=20mm] {~};\n",k)
        fmt.Printf("\\maison%s{%s}{%s};\n",maisonNames[nb_base],k,tikzState[k])
      }
    }
    fmt.Println("\\end{tikzpicture}")
*/
    fmt.Println("\\begin{tikzpicture}[ultra thick,text=white,font=\\tiny]")
//    fmt.Println("\\graph [spring electrical layout', coarsen, node distance=24mm] {")
    fmt.Println("\\graph [tree layout, coarsen, node distance=24mm] {")
    for e := range loopStates {
      if loopStates[e] {
        var f = nextState[e]
        fmt.Println ("  ",f, "[thick,circle,minimum size=20.5mm,draw=white] <- ", e, "[thick,circle,minimum size=20.5mm,draw=white];");
      }
    }
    fmt.Println("};")
    fmt.Println("% Regular (looping) States")
    for k := range nofilterStates {
      if loopStates[k] {
        fmt.Printf("\\node[at=(%s.base),thick,draw=black,circle,minimum size=20mm] {~};\n",k)
      }
    }
    fmt.Println("% (looping) States forbidden by filters")
    for k := range filterStates {
      if loopStates[k] {
        fmt.Printf("\\node[at=(%s.base),thick,draw=red,fill=red!5,circle,minimum size=20mm] {~};\n",k)
      }
    }
    fmt.Println("% All (looping) states")
    for k := range nofilterStates {
      if loopStates[k] {
        fmt.Printf("\\maison%s{%s}{%s};\n",maisonNames[nb_base],k,tikzState[k])
      }
    }
    fmt.Println("\\end{tikzpicture}")

  }
}


/*
#bases |  #states |     #looping    ||  #false home  |  #false double  |  #false both  |
-------+----------+-----------------++---------------+-----------------+---------------+
   4   |      480 |      44 (9.16%) ||     0 (0%)    |      18 (8.82%) |     0 (0%)    |
   5   |    11010 |    1748 (15.8%) ||    24 (1.31%) |     644 (14.2%) |     0 (0%)    |
   6   |   367560 |   72655 (19.8%) ||  1251 (2.12%) |   26142 (17.6%) |    85 (0.43%) |
   7   | 16854390 | 3749428 (22.2%) || 84444 (3.21%) | 1338173 (20.0%) | 11430 (1.27%) | 1m41

*/