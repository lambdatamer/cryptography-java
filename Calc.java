import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Calc {
  public static void main(String args[]) {
    print(findSqRoot(81,97));
  }

  public static void print(Object text) {
    System.out.println(String.valueOf(text));
  }

  public static long mod(long a, long b) {
    if (b <= 0) {
      throw new RuntimeException("module can\'t be <= 0");
    }
    while (a < 0) {
      a += b;
    }
    return (a % b);
  }

  public static List<Integer> primeFactors(int number) {
    int n = number;
    List<Integer> factors = new ArrayList<Integer>();
    for (int i = 2; i <= n; i++) {
      while (n % i == 0) {
        factors.add(i);
        n /= i;
      }
    }
    return factors;
  }

  public static int legandre(int a, int p, boolean logging) {
    if (logging) print("Calculating legandre(" + a + "/" + p + ")...");
    if (logging) print("It's equal to ");
    a = (int)mod(a, p);
    if (logging) print("(" + a + "/" + p + ")");

    if (a == 0 || a == 1) {
      if (logging) print("(" + a + "/" + p + ") = 1");
      return 1;
    }

    // find factors of a and throw away squares
    List<Integer> factors = primeFactors(a);
    List<Integer> nonSqrFactors = new ArrayList<Integer>();
    for (Integer factor : factors) {
      if (nonSqrFactors.contains(factor)){
        nonSqrFactors.remove(nonSqrFactors.indexOf(factor));
      } else {
        nonSqrFactors.add(factor);
      }
    }

    if (logging) {
      print("Factorization of " + a + ":");
      for (Integer factor : factors) {
        System.out.print(String.valueOf(factor) + " ");
      }
      print("\nNon-square factors: ");
      for (Integer factor : nonSqrFactors) {
        System.out.print(String.valueOf(factor) + " ");
      }
      print("");
    }

    if (nonSqrFactors.size() == 0) {
      if (logging) print("There is no non-square factors. (" + a + "/" + p + ") = 1");
      return 1;
    }

    int result = 1,
        degree,
        reminder,
        multiplier;
    if (logging) print("Calculating legandre for each factor...");
    for (Integer factor : nonSqrFactors) {
      if (logging) print("current factor: " + factor);
      if (factor == 2) {
        reminder = (int)mod(p, 8);
        if (logging){
          print(p + " mod 8 = " + reminder);
          System.out.print("(2/" + p + ") = ");
        }
        if (reminder == 1 || reminder == 7) {
          result *= 1;
          if (logging) print(1);
        } else if (reminder == 3 || reminder == 5) {
          result *= -1;
          if (logging) print(-1);
        } else {
          throw new RuntimeException("Unknown error. Check arguments.");
        }
      } else {
        degree = ((p - 1) * (factor - 1)) / 4;
        if (logging) print("(" + factor + "/" + p + ") = (-1)^" + degree + " * (" + p + "/" + factor + ")");

        if(degree % 2 == 1) {
          multiplier = -1;
        } else {
          multiplier = 1;
        }

        if (logging) print("multiplier is " + multiplier);

        if (logging) print("Going deeper into recursion...");
        result *= multiplier * legandre(p, factor, logging);
        if (logging) print("Coming back from recursion.");
      }
    }
    if (logging) print("(" + a + "/" + p + ") = " + result);
    return result;
  }

  public static long findSqRoot(long a, long p) {
    long x = 0;
    int pMod4 = (int)mod(p, 4);

    if (pMod4 == 3) {
      print(p + " = 3 mod 4");

      long m = (p - 3) / 4;
      print("m = " + m);

      BigInteger _degOfA = BigInteger.valueOf(m + 1);
      BigInteger _p = BigInteger.valueOf(p);
      BigInteger _x = BigInteger.valueOf(a);
      _x = _x.modPow(_degOfA, _p);
      print("x = " + a + "^(" + _degOfA.toString() + ") mod " + p + " = " + _x);

    } else if (pMod4 == 1) {
      print(p + " = 1 mod 4");

      long  h,
            k,
            _k = 1;

      while (p - 1 > Math.pow(2, _k)) {
        _k++;
      }

      k = --_k;

      while (((p - 1) % Math.pow(2, _k)) > 0) {
        k = --_k;
      }
      h = (p - 1) / (long)Math.pow(2, _k);
      print(p + " - 1 = 2^" + k + " * " + h);
      print("k = " + k + "; h = " + h);

      // don't sure that it's secure
      long a1 = mod((long)Math.pow(a, ((h + 1) / 2)), p);
      if (h % 2 == 0) {
        throw new RuntimeException("h is even. FIXME");
      }
      print("a1 = " + a1);

      long a2 = 1;

      while (mod(a * a2, p) != 1) {
        a2++;
      }
      print("a2 = " + a2);

      long n = 0;
      int lg;
      for (int i = 2; i < p; i++) {
        lg = legandre(i, (int)p, false);
        if ( lg == -1) {
          n = i;
          break;
        }
      }
      print("n = " + n);

      if (n == 0) {
        throw new RuntimeException("Something bad happened. Can't find (n/p) = -1");
      }

      long n1 = mod((long)Math.pow(n, h), p);
      long n2 = 1;
      print("n1 = " + n1 + "\nn2 = 1");
      long b, c, d;
      for (int i = 0; i < k - 2; i++) {
        b = mod((a1 * n2), p);
        c = mod((a2 * mod((long)Math.pow(b, 2), p)), p);
        d = mod((long)Math.pow(c, (long)Math.pow(2, k - i - 2)), p);
        print("i = " + i + "\n\tb = " + b + "\n\tc = " + c + "\n\td = " + d);
        if (d == p - 1) {
          n2 = n2 * mod((long)Math.pow(n1, Math.pow(2, i)), p);
          print("n2 = " + n2);
        } else if (d != 1) {
          throw new RuntimeException("Something bad happened. d = " + d);
        }
      }

      x = mod((a1 * n2), p);
      print("x = +- " + x);
    } else {
      throw new Error("Unknown error. Check the arguments.");
    }
    return x;
  }
}