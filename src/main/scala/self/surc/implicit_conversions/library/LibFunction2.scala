package self.surc.implicit_conversions.library

trait LibFunction2[P1, P2, R] {
  def call(p1: P1, p2: P2): R
}
