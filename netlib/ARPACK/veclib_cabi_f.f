      real function wsdot(n, x, incx, y, incy)
      real x(*), y(*), s
      integer n, incx, incy

      call veclib_sdot(n, x, incx, y, incy, s)

      wsdot = s
      return
      end
