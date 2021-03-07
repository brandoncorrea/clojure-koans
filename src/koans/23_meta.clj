(ns koans.23-meta
  (:require [koan-engine.core :refer :all]))

(def giants
  (with-meta 'Giants
    {:league "National League"}))

(meditations
  ; giants is like an object
  "Some objects can be tagged using the with-meta function"
  (= { :league "National League" } (meta giants))

  ; This always returns, whether you put Giants, giants, or abc
  ; Why?
  "Or more succinctly with a reader macro"
  (= {:division "West"} (meta '^{:division "West"} Giants))

  ; This throws because 2 is not an object?
  "While others can't"
  (= "This doesn't implement the IObj interface"
     (try
          (with-meta
            2
            {:prime true})
          (catch ClassCastException e
            "This doesn't implement the IObj interface")))

  ; Repl gave me { :foo :bar } ... but why?
  "Notice when metadata carries over"
  (= { :foo :bar }
     (meta (merge '^{:foo :bar} {:a 1 :b 2} {:b 3 :c 4})))

  ; When we switch the order, we get nil ... why?
  "And when it doesn't"
  (= nil (meta (merge {:a 1 :b 2} '^{:foo :bar} {:b 3 :c 4})))

  ; Getting char at index 0 - ok
  "Metadata can be used as a type hint to avoid reflection during runtime"
  (= \C (#(.charAt ^String % 0) "Cast me"))

  ; swap values - cool
  "You can directly update an object's metadata"
  (= 8 (let
        [
          giants
         ; Start with :world-series-titles = 7
          (with-meta 'Giants {:world-series-titles (atom 7)})
        ]
        ; increment :world-series-titles
        (swap! (:world-series-titles (meta giants)) inc)
        ; Return :world-series-titles
        @(:world-series-titles (meta giants))
    )
  )

  ; before, giants only had league. now we associate park with giants
  "You can also create a new object from another object with metadata"
  (= {:league "National League" :park "AT&T Park"}
     (meta (vary-meta giants
                      assoc :park "AT&T Park")))

  ; vary-meta returns the with-meta quoted name
  "But it won't affect behavior like equality"
  (= 'Giants (vary-meta giants dissoc :league))

  ; So because the above problem returns (quote Giants),
  ; pr-str casts that to a string
  "Or the object's printed representation"
  (= "Giants" (pr-str (vary-meta giants dissoc :league))))
