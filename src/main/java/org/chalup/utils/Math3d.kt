package org.chalup.utils

data class Vector3(val x: Long, val y: Long, val z: Long) {
    fun toVector4() = Vector4(x, y, z, 1L)

    operator fun minus(other: Vector3) = Vector3(
        x = x - other.x,
        y = y - other.y,
        z = z - other.z,
    )

    operator fun plus(other: Vector3) = Vector3(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    override fun toString(): String = "($x, $y, $z)"
}

data class Vector4(val x: Long, val y: Long, val z: Long, val w: Long) {
    fun toVector3() = Vector3(x, y, z).also { check(w == 1L) }

    operator fun times(m: Matrix4) = Vector4(
        x = x * m.xx + y * m.yx + z * m.zx + w * m.wx,
        y = x * m.xy + y * m.yy + z * m.zy + w * m.wy,
        z = x * m.xz + y * m.yz + z * m.zz + w * m.wz,
        w = x * m.xw + y * m.yw + z * m.zw + w * m.ww,
    )
}

data class Matrix4(
    val xx: Long, val xy: Long, val xz: Long, val xw: Long,
    val yx: Long, val yy: Long, val yz: Long, val yw: Long,
    val zx: Long, val zy: Long, val zz: Long, val zw: Long,
    val wx: Long, val wy: Long, val wz: Long, val ww: Long,
) {
    infix operator fun times(o: Matrix4) = Matrix4(
        xx = xx * o.xx + xy * o.yx + xz * o.zx + xw * o.wx,
        xy = xx * o.xy + xy * o.yy + xz * o.zy + xw * o.wy,
        xz = xx * o.xz + xy * o.yz + xz * o.zz + xw * o.wz,
        xw = xx * o.xw + xy * o.yw + xz * o.zw + xw * o.ww,

        yx = yx * o.xx + yy * o.yx + yz * o.zx + yw * o.wx,
        yy = yx * o.xy + yy * o.yy + yz * o.zy + yw * o.wy,
        yz = yx * o.xz + yy * o.yz + yz * o.zz + yw * o.wz,
        yw = yx * o.xw + yy * o.yw + yz * o.zw + yw * o.ww,

        zx = zx * o.xx + zy * o.yx + zz * o.zx + zw * o.wx,
        zy = zx * o.xy + zy * o.yy + zz * o.zy + zw * o.wy,
        zz = zx * o.xz + zy * o.yz + zz * o.zz + zw * o.wz,
        zw = zx * o.xw + zy * o.yw + zz * o.zw + zw * o.ww,

        wx = wx * o.xx + wy * o.yx + wz * o.zx + ww * o.wx,
        wy = wx * o.xy + wy * o.yy + wz * o.zy + ww * o.wy,
        wz = wx * o.xz + wy * o.yz + wz * o.zz + ww * o.wz,
        ww = wx * o.xw + wy * o.yw + wz * o.zw + ww * o.ww,
    )

    companion object {
        val IDENTITY = Matrix4(
            xx = 1, xy = 0, xz = 0, xw = 0,
            yx = 0, yy = 1, yz = 0, yw = 0,
            zx = 0, zy = 0, zz = 1, zw = 0,
            wx = 0, wy = 0, wz = 0, ww = 1,
        )

        fun translation(v: Vector3) = Matrix4(
            xx = 1, xy = 0, xz = 0, xw = 0,
            yx = 0, yy = 1, yz = 0, yw = 0,
            zx = 0, zy = 0, zz = 1, zw = 0,
            wx = v.x, wy = v.y, wz = v.z, ww = 1,
        )

        fun rotation(m: Matrix3) = Matrix4(
            xx = m.xx, xy = m.xy, xz = m.xz, xw = 0,
            yx = m.yx, yy = m.yy, yz = m.yz, yw = 0,
            zx = m.zx, zy = m.zy, zz = m.zz, zw = 0,
            wx = 0, wy = 0, wz = 0, ww = 1,
        )
    }
}

data class Matrix3(
    val xx: Long, val xy: Long, val xz: Long,
    val yx: Long, val yy: Long, val yz: Long,
    val zx: Long, val zy: Long, val zz: Long,
) {
    val det: Long = xx * (yy * zz - yz * zy) - xy * (yx * zz - yz * zx) + xz * (yx * zy - yy * zx)

    infix operator fun times(o: Matrix3) = Matrix3(
        xx = xx * o.xx + xy * o.yx + xz * o.zx,
        xy = xx * o.xy + xy * o.yy + xz * o.zy,
        xz = xx * o.xz + xy * o.yz + xz * o.zz,

        yx = yx * o.xx + yy * o.yx + yz * o.zx,
        yy = yx * o.xy + yy * o.yy + yz * o.zy,
        yz = yx * o.xz + yy * o.yz + yz * o.zz,

        zx = zx * o.xx + zy * o.yx + zz * o.zx,
        zy = zx * o.xy + zy * o.yy + zz * o.zy,
        zz = zx * o.xz + zy * o.yz + zz * o.zz,
    )
}

val rotations3d: Set<Matrix4> = buildSet {
    for (x in 0..2)
        for (y in 0..2) {
            if (x == y) continue
            for (z in 0..2) {
                if (z == x || z == y) continue

                for (sx in -1..1 step 2)
                    for (sy in -1..1 step 2)
                        for (sz in -1..1 step 2) {
                            val m = MutableList(9) { 0L }
                            m[x] = sx.toLong()
                            m[3 + y] = sy.toLong()
                            m[6 + z] = sz.toLong()

                            val rotationMatrix = Matrix3(
                                m[0], m[1], m[2],
                                m[3], m[4], m[5],
                                m[6], m[7], m[8],
                            )

                            if (rotationMatrix.det == 1L) {
                                add(Matrix4.rotation(rotationMatrix))
                            }
                        }
            }
        }
}


