package com.lc.nlp4han.ml.util;

public interface SequenceValidator<T> {

  /**
   * Determines whether a particular continuation of a sequence is valid.
   * This is used to restrict invalid sequences such as those used in start/continue tag-based chunking
   * or could be used to implement tag dictionary restrictions.
   *
   * @param i The index in the input sequence for which the new outcome is being proposed.
   * @param inputSequence The input sequence.
   * @param outcomesSequence The outcomes so far in this sequence.
   * @param outcome The next proposed outcome for the outcomes sequence.
   *
   * @return true is the sequence would still be valid with the new outcome, false otherwise.
   */
  boolean validSequence(int i, T[] inputSequence, String[] outcomesSequence,
      String outcome);
}
