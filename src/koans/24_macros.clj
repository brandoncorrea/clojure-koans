(ns koans.24-macros
  (:require [koan-engine.core :refer :all]))

(defmacro hello [x]
  (str "Hello, " x))

(defmacro infix [form]
  (list (second form) (first form) (nth form 2)))

(defmacro infix-concise [form]
  `(~(second form) ; Note the syntax-quote (`) and unquote (~) characters!
    ~(first form)
    ~(nth form 2)))

(defmacro recursive-infix [form]
  (cond
    ; if this is not a sequence, then it is a value
    (not (seq? form))
      form
    ; If this sequence has one item, then it is a (...)
    (= 1 (count form))
      `(recursive-infix ~(first form))
    ; We have stuff to play with...
    :else
      (let [operator (second form)
              first-arg (first form)
              others (drop 2 form)]
        `(~operator
         (recursive-infix ~first-arg)
         (recursive-infix ~others))
      )
  )
)

(meditations
  "Macros are like functions created at compile time"
  (= "Hello, Macros!" (hello "Macros!"))

  "I can haz infix?"
  (= 10 (infix (9 + 1)))

  ; '(infix (9 + 1)) => (infix (9 + 1)
  ; Note: no beginning quote
  ; macroexpand removes the macro name and top-level parentheses
  "Remember, these are nothing but code transformations"
  (= '(+ 9 1) (macroexpand '(infix (9 + 1))))

  "You can do better than that - hand crafting FTW!"
  (= '(* 10 2) (macroexpand '(infix-concise (10 * 2))))

  "Things don't always work as you would like them to... "
  ; ... Or do they?
  ; This passed when I solved the problem above but I have no idea why
  (= '(+ 10 (2 * 3)) (macroexpand '(infix-concise (10 + (2 * 3)))))

  "Really, you don't understand recursion until you understand recursion"
  (= 36 (recursive-infix (10 + (2 * 3) + (4 * 5)))))
