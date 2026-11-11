// ========
// vector.h
// ========

#ifndef _COMMON_VECTOR_H_
#define _COMMON_VECTOR_H_

#include "common.h"

class Vec2
{
public:
	Vec2() {}
	Vec2(float x_, float y_) : x(x_), y(y_) {}
	Vec2(float f_) : x(f_), y(f_) {}

	const Vec2 operator +() const { return *this; }
	const Vec2 operator -() const { return Vec2(-x, -y); }

	const Vec2 operator +(const Vec2& rhs) const { return Vec2(x + rhs.x, y + rhs.y); }
	const Vec2 operator -(const Vec2& rhs) const { return Vec2(x - rhs.x, y - rhs.y); }
	const Vec2 operator *(const Vec2& rhs) const { return Vec2(x * rhs.x, y * rhs.y); }
	const Vec2 operator /(const Vec2& rhs) const { return Vec2(x / rhs.x, y / rhs.y); }

	const Vec2 operator *(float rhs) const { return Vec2(x * rhs, y * rhs); }
	const Vec2 operator /(float rhs) const { return Vec2(x / rhs, y / rhs); }

	Vec2& operator +=(const Vec2& rhs) { (*this) = (*this) + rhs; return (*this); }
	Vec2& operator -=(const Vec2& rhs) { (*this) = (*this) - rhs; return (*this); }
	Vec2& operator *=(const Vec2& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec2& operator /=(const Vec2& rhs) { (*this) = (*this) / rhs; return (*this); }

	Vec2& operator *=(float rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec2& operator /=(float rhs) { (*this) = (*this) / rhs; return (*this); }

	float x, y;
};

class Vec3
{
public:
	Vec3() {}
	Vec3(float x_, float y_, float z_) : x(x_), y(y_), z(z_) {}
	Vec3(float f_) : x(f_), y(f_), z(f_) {}

	const Vec3 operator +() const { return *this; }
	const Vec3 operator -() const { return Vec3(-x, -y, -z); }

	const Vec3 operator +(const Vec3& rhs) const { return Vec3(x + rhs.x, y + rhs.y, z + rhs.z); }
	const Vec3 operator -(const Vec3& rhs) const { return Vec3(x - rhs.x, y - rhs.y, z - rhs.z); }
	const Vec3 operator *(const Vec3& rhs) const { return Vec3(x * rhs.x, y * rhs.y, z * rhs.z); }
	const Vec3 operator /(const Vec3& rhs) const { return Vec3(x / rhs.x, y / rhs.y, z / rhs.z); }

	const Vec3 operator *(float rhs) const { return Vec3(x * rhs, y * rhs, z * rhs); }
	const Vec3 operator /(float rhs) const { return Vec3(x / rhs, y / rhs, z / rhs); }

	Vec3& operator +=(const Vec3& rhs) { (*this) = (*this) + rhs; return (*this); }
	Vec3& operator -=(const Vec3& rhs) { (*this) = (*this) - rhs; return (*this); }
	Vec3& operator *=(const Vec3& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec3& operator /=(const Vec3& rhs) { (*this) = (*this) / rhs; return (*this); }

	Vec3& operator *=(float rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec3& operator /=(float rhs) { (*this) = (*this) / rhs; return (*this); }

	float x, y, z;
};

class Vec4
{
public:
	Vec4() {}
	Vec4(float x_, float y_, float z_, float w_) : x(x_), y(y_), z(z_), w(w_) {}
	Vec4(float f_) : x(f_), y(f_), z(f_), w(f_) {}

	const Vec4 operator +() const { return *this; }
	const Vec4 operator -() const { return Vec4(-x, -y, -z, -w); }

	const Vec4 operator +(const Vec4& rhs) const { return Vec4(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w); }
	const Vec4 operator -(const Vec4& rhs) const { return Vec4(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w); }
	const Vec4 operator *(const Vec4& rhs) const { return Vec4(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w); }
	const Vec4 operator /(const Vec4& rhs) const { return Vec4(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w); }

	const Vec4 operator *(float rhs) const { return Vec4(x * rhs, y * rhs, z * rhs, w * rhs); }
	const Vec4 operator /(float rhs) const { return Vec4(x / rhs, y / rhs, z / rhs, w / rhs); }

	Vec4& operator +=(const Vec4& rhs) { (*this) = (*this) + rhs; return (*this); }
	Vec4& operator -=(const Vec4& rhs) { (*this) = (*this) - rhs; return (*this); }
	Vec4& operator *=(const Vec4& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec4& operator /=(const Vec4& rhs) { (*this) = (*this) / rhs; return (*this); }

	Vec4& operator *=(float rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec4& operator /=(float rhs) { (*this) = (*this) / rhs; return (*this); }

	float x, y, z, w;
};

class Pixel32
{
public:
	Pixel32() {}
	Pixel32(u32 bgra_) : bgra(bgra_) {}
	Pixel32(int r_, int g_, int b_, int a_) : r((u8)r_), g((u8)g_), b((u8)b_), a((u8)a_) {}

	bool operator ==(const Pixel32& rhs) const { return r == rhs.r && g == rhs.g && b == rhs.b && a == rhs.a; }
	bool operator !=(const Pixel32& rhs) const { return r != rhs.r || g != rhs.g || b != rhs.b || a != rhs.a; }

	union
	{
		struct { u8 b, g, r, a; };
		struct { u32 bgra; };
	};
};

class Pixel64
{
public:
	Pixel64() {}
	Pixel64(u64 bgra_) : bgra(bgra_) {}
	Pixel64(int r_, int g_, int b_, int a_) : r((u16)r_), g((u16)g_), b((u16)b_), a((u16)a_) {}

	bool operator ==(const Pixel64& rhs) const { return r == rhs.r && g == rhs.g && b == rhs.b && a == rhs.a; }
	bool operator !=(const Pixel64& rhs) const { return r != rhs.r || g != rhs.g || b != rhs.b || a != rhs.a; }

	union
	{
		struct { u16 b, g, r, a; };
		struct { u64 bgra; };
	};
};

enum
{
	_unknown,
	_u8,
	_u16,
	_float,
	_Vec3,
	_Vec4,
	_Pixel32,
	_Pixel64,
};

template <typename T> class GetType { public: enum { N = 0, T = _unknown }; };

template <> class GetType<u8     > { public: enum { N = 1, T = _u8      }; };
template <> class GetType<u16    > { public: enum { N = 1, T = _u16     }; };
template <> class GetType<float  > { public: enum { N = 1, T = _float   }; };
template <> class GetType<Vec3   > { public: enum { N = 3, T = _Vec3    }; };
template <> class GetType<Vec4   > { public: enum { N = 4, T = _Vec4    }; };
template <> class GetType<Pixel32> { public: enum { N = 4, T = _Pixel32 }; };
template <> class GetType<Pixel64> { public: enum { N = 4, T = _Pixel64 }; };

template <typename DstType, typename SrcType> __forceinline DstType ConvertPixel(const SrcType& src);

template <> __forceinline u8 ConvertPixel<u8,u8>(const u8& p) { return p; }
template <> __forceinline u16 ConvertPixel<u16,u8>(const u8& p) { const u16 q = p; return q|(q<<8); }
template <> __forceinline float ConvertPixel<float,u8>(const u8& p) { return (float)p/255.0f; }
template <> __forceinline Vec3 ConvertPixel<Vec3,u8>(const u8& p) { const float q = ConvertPixel<float,u8>(p); return Vec3(q, q, q); }
template <> __forceinline Vec4 ConvertPixel<Vec4,u8>(const u8& p) { const float q = ConvertPixel<float,u8>(p); return Vec4(q, q, q, 1.0f); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,u8>(const u8& p) { return Pixel32(p, p, p, 255); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,u8>(const u8& p) { const u16 q = ConvertPixel<u16,u8>(p); return Pixel64(q, q, q, 65535); }

template <> __forceinline u8 ConvertPixel<u8,u16>(const u16& p) { return (u8)(p>>8); }
template <> __forceinline u16 ConvertPixel<u16,u16>(const u16& p) { return p; }
template <> __forceinline float ConvertPixel<float,u16>(const u16& p) { return (float)p/65535.0f; }
template <> __forceinline Vec3 ConvertPixel<Vec3,u16>(const u16& p) { const float q = ConvertPixel<float,u16>(p); return Vec3(q, q, q); }
template <> __forceinline Vec4 ConvertPixel<Vec4,u16>(const u16& p) { const float q = ConvertPixel<float,u16>(p); return Vec4(q, q, q, 1.0f); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,u16>(const u16& p) { const u8 q = ConvertPixel<u8,u16>(p); return Pixel32(q, q, q, 255); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,u16>(const u16& p) { return Pixel64(p, p, p, 65535); }

template <> __forceinline u8 ConvertPixel<u8,float>(const float& p) { return (u8)Clamp<float>(0.5f + 255.0f*p, 0.0f, 255.0f); }
template <> __forceinline u16 ConvertPixel<u16,float>(const float& p) { return (u16)Clamp<float>(0.5f + 65535.0f*p, 0.0f, 65535.0f); }
template <> __forceinline float ConvertPixel<float,float>(const float& p) { return p; }
template <> __forceinline Vec3 ConvertPixel<Vec3,float>(const float& p) { return Vec3(p, p, p); }
template <> __forceinline Vec4 ConvertPixel<Vec4,float>(const float& p) { return Vec4(p, p, p, 1.0f); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,float>(const float& p) { const u8 q = ConvertPixel<u8,float>(p); return Pixel32(q, q, q, 255); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,float>(const float& p) { const u16 q = ConvertPixel<u16,float>(p); return Pixel64(q, q, q, 65535); }

template <> __forceinline u8 ConvertPixel<u8,Vec3>(const Vec3& p) { return ConvertPixel<u8,float>(p.x); }
template <> __forceinline u16 ConvertPixel<u16,Vec3>(const Vec3& p) { return ConvertPixel<u16,float>(p.x); }
template <> __forceinline float ConvertPixel<float,Vec3>(const Vec3& p) { return p.x; }
template <> __forceinline Vec3 ConvertPixel<Vec3,Vec3>(const Vec3& p) { return p; }
template <> __forceinline Vec4 ConvertPixel<Vec4,Vec3>(const Vec3& p) { return Vec4(p.x, p.y, p.z, 1.0f); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,Vec3>(const Vec3& p) { return Pixel32(ConvertPixel<u8,float>(p.x), ConvertPixel<u8,float>(p.y), ConvertPixel<u8,float>(p.z), 255); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,Vec3>(const Vec3& p) { return Pixel64(ConvertPixel<u16,float>(p.x), ConvertPixel<u16,float>(p.y), ConvertPixel<u16,float>(p.z), 65535); }

template <> __forceinline u8 ConvertPixel<u8,Vec4>(const Vec4& p) { return ConvertPixel<u8,float>(p.x); }
template <> __forceinline u16 ConvertPixel<u16,Vec4>(const Vec4& p) { return ConvertPixel<u16,float>(p.x); }
template <> __forceinline float ConvertPixel<float,Vec4>(const Vec4& p) { return p.x; }
template <> __forceinline Vec3 ConvertPixel<Vec3,Vec4>(const Vec4& p) { return Vec3(p.x, p.y, p.z); }
template <> __forceinline Vec4 ConvertPixel<Vec4,Vec4>(const Vec4& p) { return p; }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,Vec4>(const Vec4& p) { return Pixel32(ConvertPixel<u8,float>(p.x), ConvertPixel<u8,float>(p.y), ConvertPixel<u8,float>(p.z), ConvertPixel<u8,float>(p.w)); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,Vec4>(const Vec4& p) { return Pixel64(ConvertPixel<u16,float>(p.x), ConvertPixel<u16,float>(p.y), ConvertPixel<u16,float>(p.z), ConvertPixel<u16,float>(p.w)); }

template <> __forceinline u8 ConvertPixel<u8,Pixel32>(const Pixel32& p) { return p.r; }
template <> __forceinline u16 ConvertPixel<u16,Pixel32>(const Pixel32& p) { return ConvertPixel<u16,u8>(p.r); }
template <> __forceinline float ConvertPixel<float,Pixel32>(const Pixel32& p) { return ConvertPixel<float,u8>(p.r); }
template <> __forceinline Vec3 ConvertPixel<Vec3,Pixel32>(const Pixel32& p) { return Vec3(ConvertPixel<float,u8>(p.r), ConvertPixel<float,u8>(p.g), ConvertPixel<float,u8>(p.b)); }
template <> __forceinline Vec4 ConvertPixel<Vec4,Pixel32>(const Pixel32& p) { return Vec4(ConvertPixel<float,u8>(p.r), ConvertPixel<float,u8>(p.g), ConvertPixel<float,u8>(p.b), ConvertPixel<float,u8>(p.a)); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,Pixel32>(const Pixel32& p) { return p; }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,Pixel32>(const Pixel32& p) { return Pixel64(ConvertPixel<u16,u8>(p.r), ConvertPixel<u16,u8>(p.g), ConvertPixel<u16,u8>(p.b), ConvertPixel<u16,u8>(p.a)); }

template <> __forceinline u8 ConvertPixel<u8,Pixel64>(const Pixel64& p) { return ConvertPixel<u8,u16>(p.r); }
template <> __forceinline u16 ConvertPixel<u16,Pixel64>(const Pixel64& p) { return p.r; }
template <> __forceinline float ConvertPixel<float,Pixel64>(const Pixel64& p) { return ConvertPixel<float,u16>(p.r); }
template <> __forceinline Vec3 ConvertPixel<Vec3,Pixel64>(const Pixel64& p) { return Vec3(ConvertPixel<float,u16>(p.r), ConvertPixel<float,u16>(p.g), ConvertPixel<float,u16>(p.b)); }
template <> __forceinline Vec4 ConvertPixel<Vec4,Pixel64>(const Pixel64& p) { return Vec4(ConvertPixel<float,u16>(p.r), ConvertPixel<float,u16>(p.g), ConvertPixel<float,u16>(p.b), ConvertPixel<float,u16>(p.a)); }
template <> __forceinline Pixel32 ConvertPixel<Pixel32,Pixel64>(const Pixel64& p) { return Pixel32(ConvertPixel<u8,u16>(p.r), ConvertPixel<u8,u16>(p.g), ConvertPixel<u8,u16>(p.b), ConvertPixel<u8,u16>(p.a)); }
template <> __forceinline Pixel64 ConvertPixel<Pixel64,Pixel64>(const Pixel64& p) { return p; }

// =========================================================================

#define INIT_VECV(def) \
	def(V_ZERO        , 0.00f, 0.00f, 0.00f, 0.00f); \
	def(V_ONE         , 1.00f, 1.00f, 1.00f, 1.00f); \
	def(V_TWO         , 2.00f, 2.00f, 2.00f, 2.00f); \
	def(V_THREE       , 3.00f, 3.00f, 3.00f, 3.00f); \
	def(V_FOUR        , 4.00f, 4.00f, 4.00f, 4.00f); \
	def(V_NEGONE      ,-1.00f,-1.00f,-1.00f,-1.00f); \
	def(V_HALF        , 0.50f, 0.50f, 0.50f, 0.50f); \
	def(V_QUARTER     , 0.25f, 0.25f, 0.25f, 0.25f); \
	def(V_X_AXIS_WZERO, 1.00f, 0.00f, 0.00f, 0.00f); \
	def(V_Y_AXIS_WZERO, 0.00f, 1.00f, 0.00f, 0.00f); \
	def(V_Z_AXIS_WZERO, 0.00f, 0.00f, 1.00f, 0.00f);

#define VECV_ENUM(name,x_,y_,z_,w_) enum e##name { name };
INIT_VECV(VECV_ENUM);
#undef VECV_ENUM

// ================================
__declspec(align(16)) class ScalarV
{
public:
	ScalarV() {}
	ScalarV(float f_) : x(f_), y(f_), z(f_), w(f_) {}

	#define SCALARV_CTOR(name,x_,y_,z_,w_) ScalarV(e##name) : x(x_), y(y_), z(z_), w(w_) {}
	INIT_VECV(SCALARV_CTOR);
	#undef SCALARV_CTOR

	float Getf() const { return x; }

	const ScalarV operator +() const { return *this; }
	const ScalarV operator -() const { return ScalarV(-x); }

	const ScalarV operator +(const ScalarV& rhs) const { return ScalarV(x + rhs.x); }
	const ScalarV operator -(const ScalarV& rhs) const { return ScalarV(x - rhs.x); }
	const ScalarV operator *(const ScalarV& rhs) const { return ScalarV(x * rhs.x); }
	const ScalarV operator /(const ScalarV& rhs) const { return ScalarV(x / rhs.x); }

	float x, y, z, w;
};

typedef const ScalarV& ScalarV_In;
typedef const ScalarV ScalarV_Out;
typedef ScalarV& ScalarV_InOut;

__forceinline ScalarV_Out Min(ScalarV_In a, ScalarV_In b) { return ScalarV(Min<float>(a.x, b.x)); }
__forceinline ScalarV_Out Max(ScalarV_In a, ScalarV_In b) { return ScalarV(Max<float>(a.x, b.x)); }
__forceinline ScalarV_Out Abs(ScalarV_In a) { return ScalarV(Abs<float>(a.x)); }
__forceinline ScalarV_Out Clamp(ScalarV_In a, ScalarV_In b, ScalarV_In c) { return ScalarV(Clamp<float>(a.x, b.x, c.x)); }

__forceinline ScalarV_Out Min(ScalarV_In a, ScalarV_In b, ScalarV_In c) { return ScalarV(Min<float>(a.x, b.x, c.x)); }
__forceinline ScalarV_Out Max(ScalarV_In a, ScalarV_In b, ScalarV_In c) { return ScalarV(Max<float>(a.x, b.x, c.x)); }

__forceinline int IsGreaterThanAll       (ScalarV_In a, ScalarV_In b) { return (a.x >  b.x) ? 1 : 0; }
__forceinline int IsGreaterThanOrEqualAll(ScalarV_In a, ScalarV_In b) { return (a.x >= b.x) ? 1 : 0; }
__forceinline int IsLessThanAll          (ScalarV_In a, ScalarV_In b) { return (a.x <  b.x) ? 1 : 0; }
__forceinline int IsLessThanOrEqualAll   (ScalarV_In a, ScalarV_In b) { return (a.x <= b.x) ? 1 : 0; }
__forceinline int IsEqualAll             (ScalarV_In a, ScalarV_In b) { return (a.x == b.x) ? 1 : 0; }

__forceinline ScalarV_Out Sqrt(ScalarV_In a) { return ScalarV(sqrtf(a.x)); }

// ==============================
__declspec(align(16)) class Vec3V
{
public:
	Vec3V() {}
	Vec3V(float x_, float y_, float z_) : x(x_), y(y_), z(z_), w_unused(0.0f) {}
	Vec3V(float f_) : x(f_), y(f_), z(f_), w_unused(f_) {}
	Vec3V(ScalarV_In f_) : x(f_.x), y(f_.y), z(f_.z), w_unused(f_.w) {}
	Vec3V(ScalarV_In x_, ScalarV_In y_, ScalarV_In z_) : x(x_.x), y(y_.x), z(z_.x), w_unused(0.0f) {}

	#define VEC3V_CTOR(name,x_,y_,z_,w_) Vec3V(e##name) : x(x_), y(y_), z(z_), w_unused(w_) {}
	INIT_VECV(VEC3V_CTOR);
	#undef VEC3V_CTOR

	float GetXf() const { return x; }
	float GetYf() const { return y; }
	float GetZf() const { return z; }

	ScalarV_Out GetX() const { return ScalarV(x); }
	ScalarV_Out GetY() const { return ScalarV(y); }
	ScalarV_Out GetZ() const { return ScalarV(z); }

	const Vec3V operator +() const { return *this; }
	const Vec3V operator -() const { return Vec3V(-x, -y, -z); }

	const Vec3V operator +(const Vec3V& rhs) const { return Vec3V(x + rhs.x, y + rhs.y, z + rhs.z); }
	const Vec3V operator -(const Vec3V& rhs) const { return Vec3V(x - rhs.x, y - rhs.y, z - rhs.z); }
	const Vec3V operator *(const Vec3V& rhs) const { return Vec3V(x * rhs.x, y * rhs.y, z * rhs.z); }
	const Vec3V operator /(const Vec3V& rhs) const { return Vec3V(x / rhs.x, y / rhs.y, z / rhs.z); }

	const Vec3V operator *(const ScalarV& rhs) const { return Vec3V(x * rhs.x, y * rhs.y, z * rhs.z); }
	const Vec3V operator /(const ScalarV& rhs) const { return Vec3V(x / rhs.x, y / rhs.y, z / rhs.z); }

	Vec3V& operator +=(const Vec3V& rhs) { (*this) = (*this) + rhs; return (*this); }
	Vec3V& operator -=(const Vec3V& rhs) { (*this) = (*this) - rhs; return (*this); }
	Vec3V& operator *=(const Vec3V& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec3V& operator /=(const Vec3V& rhs) { (*this) = (*this) / rhs; return (*this); }

	Vec3V& operator *=(const ScalarV& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec3V& operator /=(const ScalarV& rhs) { (*this) = (*this) / rhs; return (*this); }

	float x, y, z, w_unused;
};

typedef const Vec3V& Vec3V_In;
typedef const Vec3V Vec3V_Out;
typedef Vec3V& Vec3V_InOut;

__forceinline Vec3V_Out Min(Vec3V_In a, Vec3V_In b) { return Vec3V(Min<float>(a.x, b.x), Min<float>(a.y, b.y), Min<float>(a.z, b.z)); }
__forceinline Vec3V_Out Max(Vec3V_In a, Vec3V_In b) { return Vec3V(Max<float>(a.x, b.x), Max<float>(a.y, b.y), Max<float>(a.z, b.z)); }
__forceinline Vec3V_Out Abs(Vec3V_In a) { return Vec3V(Abs<float>(a.x), Abs<float>(a.y), Abs<float>(a.z)); }
__forceinline Vec3V_Out Clamp(Vec3V_In a, Vec3V_In b, Vec3V_In c) { return Vec3V(Clamp<float>(a.x, b.x, c.x), Clamp<float>(a.y, b.y, c.y), Clamp<float>(a.z, b.z, c.z)); }

__forceinline Vec3V_Out Min(Vec3V_In a, Vec3V_In b, Vec3V_In c) { return Vec3V(Min<float>(a.x, b.x, c.x), Min<float>(a.y, b.y, c.y), Min<float>(a.z, b.z, c.z)); }
__forceinline Vec3V_Out Max(Vec3V_In a, Vec3V_In b, Vec3V_In c) { return Vec3V(Max<float>(a.x, b.x, c.x), Max<float>(a.y, b.y, c.y), Max<float>(a.z, b.z, c.z)); }

__forceinline int IsGreaterThanAll       (Vec3V_In a, Vec3V_In b) { return (a.x >  b.x && a.y >  b.y && a.z >  b.z) ? 1 : 0; }
__forceinline int IsGreaterThanOrEqualAll(Vec3V_In a, Vec3V_In b) { return (a.x >= b.x && a.y >= b.y && a.z >= b.z) ? 1 : 0; }
__forceinline int IsLessThanAll          (Vec3V_In a, Vec3V_In b) { return (a.x <  b.x && a.y <  b.y && a.z <  b.z) ? 1 : 0; }
__forceinline int IsLessThanOrEqualAll   (Vec3V_In a, Vec3V_In b) { return (a.x <= b.x && a.y <= b.y && a.z <= b.z) ? 1 : 0; }
__forceinline int IsEqualAll             (Vec3V_In a, Vec3V_In b) { return (a.x == b.x && a.y == b.y && a.z == b.z) ? 1 : 0; }

__forceinline Vec3V_Out Sqrt(Vec3V_In a) { return Vec3V(sqrtf(a.x), sqrtf(a.y), sqrtf(a.z)); }
__forceinline ScalarV_Out Dot(Vec3V_In a, Vec3V_In b) { return ScalarV(a.x*b.x + a.y*b.y + a.z*b.z); }
__forceinline ScalarV_Out Mag(Vec3V_In a) { return Sqrt(Dot(a, a)); }
__forceinline Vec3V_Out Normalize(Vec3V_In a) { return a/Mag(a); }

__forceinline Vec3V_Out Cross(Vec3V_In a, Vec3V_In b) { return Vec3V(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x); }

// ==============================
__declspec(align(16)) class Vec4V
{
public:
	Vec4V() {}
	Vec4V(float x_, float y_, float z_, float w_) : x(x_), y(y_), z(z_), w(w_) {}
	Vec4V(float f_) : x(f_), y(f_), z(f_), w(f_) {}
	Vec4V(ScalarV_In f_) : x(f_.x), y(f_.y), z(f_.z), w(f_.w) {}
	Vec4V(ScalarV_In x_, ScalarV_In y_, ScalarV_In z_, ScalarV_In w_) : x(x_.x), y(y_.x), z(z_.x), w(w_.x) {}
	Vec4V(Vec3V_In xyz_, ScalarV_In w_) : x(xyz_.x), y(xyz_.y), z(xyz_.z), w(w_.x) {}

	#define VEC4V_CTOR(name,x_,y_,z_,w_) Vec4V(e##name) : x(x_), y(y_), z(z_), w(w_) {}
	INIT_VECV(VEC4V_CTOR);
	#undef VEC4V_CTOR

	float GetXf() const { return x; }
	float GetYf() const { return y; }
	float GetZf() const { return z; }
	float GetWf() const { return w; }

	ScalarV_Out GetX() const { return ScalarV(x); }
	ScalarV_Out GetY() const { return ScalarV(y); }
	ScalarV_Out GetZ() const { return ScalarV(z); }
	ScalarV_Out GetW() const { return ScalarV(w); }

	Vec3V_Out GetXYZ() const { return Vec3V(x, y, z); }

	const Vec4V operator +() const { return *this; }
	const Vec4V operator -() const { return Vec4V(-x, -y, -z, -w); }

	const Vec4V operator +(const Vec4V& rhs) const { return Vec4V(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w); }
	const Vec4V operator -(const Vec4V& rhs) const { return Vec4V(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w); }
	const Vec4V operator *(const Vec4V& rhs) const { return Vec4V(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w); }
	const Vec4V operator /(const Vec4V& rhs) const { return Vec4V(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w); }

	const Vec4V operator *(const ScalarV& rhs) const { return Vec4V(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w); }
	const Vec4V operator /(const ScalarV& rhs) const { return Vec4V(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w); }

	Vec4V& operator +=(const Vec4V& rhs) { (*this) = (*this) + rhs; return (*this); }
	Vec4V& operator -=(const Vec4V& rhs) { (*this) = (*this) - rhs; return (*this); }
	Vec4V& operator *=(const Vec4V& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec4V& operator /=(const Vec4V& rhs) { (*this) = (*this) / rhs; return (*this); }

	Vec4V& operator *=(const ScalarV& rhs) { (*this) = (*this) * rhs; return (*this); }
	Vec4V& operator /=(const ScalarV& rhs) { (*this) = (*this) / rhs; return (*this); }

	float x, y, z, w;
};

typedef const Vec4V& Vec4V_In;
typedef const Vec4V Vec4V_Out;
typedef Vec4V& Vec4V_InOut;

__forceinline Vec4V_Out Min(Vec4V_In a, Vec4V_In b) { return Vec4V(Min<float>(a.x, b.x), Min<float>(a.y, b.y), Min<float>(a.z, b.z), Min<float>(a.w, b.w)); }
__forceinline Vec4V_Out Max(Vec4V_In a, Vec4V_In b) { return Vec4V(Max<float>(a.x, b.x), Max<float>(a.y, b.y), Max<float>(a.z, b.z), Max<float>(a.w, b.w)); }
__forceinline Vec4V_Out Abs(Vec4V_In a) { return Vec4V(Abs<float>(a.x), Abs<float>(a.y), Abs<float>(a.z), Abs<float>(a.w)); }
__forceinline Vec4V_Out Clamp(Vec4V_In a, Vec4V_In b, Vec4V_In c) { return Vec4V(Clamp<float>(a.x, b.x, c.x), Clamp<float>(a.y, b.y, c.y), Clamp<float>(a.z, b.z, c.z), Clamp<float>(a.w, b.w, c.w)); }

__forceinline Vec4V_Out Min(Vec4V_In a, Vec4V_In b, Vec4V_In c) { return Vec4V(Min<float>(a.x, b.x, c.x), Min<float>(a.y, b.y, c.y), Min<float>(a.z, b.z, c.z), Min<float>(a.w, b.w, c.w)); }
__forceinline Vec4V_Out Max(Vec4V_In a, Vec4V_In b, Vec4V_In c) { return Vec4V(Max<float>(a.x, b.x, c.x), Max<float>(a.y, b.y, c.y), Max<float>(a.z, b.z, c.z), Max<float>(a.w, b.w, c.w)); }

__forceinline int IsGreaterThanAll       (Vec4V_In a, Vec4V_In b) { return (a.x >  b.x && a.y >  b.y && a.z >  b.z && a.w >  b.w) ? 1 : 0; }
__forceinline int IsGreaterThanOrEqualAll(Vec4V_In a, Vec4V_In b) { return (a.x >= b.x && a.y >= b.y && a.z >= b.z && a.w >= b.w) ? 1 : 0; }
__forceinline int IsLessThanAll          (Vec4V_In a, Vec4V_In b) { return (a.x <  b.x && a.y <  b.y && a.z <  b.z && a.w <  b.w) ? 1 : 0; }
__forceinline int IsLessThanOrEqualAll   (Vec4V_In a, Vec4V_In b) { return (a.x <= b.x && a.y <= b.y && a.z <= b.z && a.w <= b.w) ? 1 : 0; }
__forceinline int IsEqualAll             (Vec4V_In a, Vec4V_In b) { return (a.x == b.x && a.y == b.y && a.z == b.z && a.w == b.w) ? 1 : 0; }

__forceinline Vec4V_Out Sqrt(Vec4V_In a) { return Vec4V(sqrtf(a.x), sqrtf(a.y), sqrtf(a.z), sqrtf(a.w)); }
__forceinline ScalarV_Out Dot(Vec4V_In a, Vec4V_In b) { return ScalarV(a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w); }
__forceinline ScalarV_Out Mag(Vec4V_In a) { return Sqrt(Dot(a, a)); }
__forceinline Vec4V_Out Normalize(Vec4V_In a) { return a/Mag(a); }

// ================================================================================================

__forceinline ScalarV_Out PlaneDistanceTo(Vec4V_In plane, Vec3V_In p)
{
	return Dot(plane.GetXYZ(), p) + plane.GetW();
}

__forceinline Vec4V_Out BuildPlane(Vec3V_In p, Vec3V_In n)
{
	return Vec4V(n, -Dot(p, n));
}

__forceinline Vec4V_Out BuildPlane(Vec3V_In p0, Vec3V_In p1, Vec3V_In p2)
{
	return BuildPlane(p0, Normalize(Cross(p1 - p0, p2 - p0)));
}

__forceinline int PolyClip(Vec3V* dst, int dstCountMax, const Vec3V* src, int srcCount, Vec4V_In plane)
{
	int dstCount = 0;

	Vec3V   p0 = src[srcCount - 1];
	ScalarV d0 = PlaneDistanceTo(plane, p0);
	u32     g0 = IsGreaterThanOrEqualAll(d0, ScalarV(V_ZERO));

	for (int i = 0; i < srcCount; i++)
	{
		const Vec3V   p1 = src[i];
		const ScalarV d1 = PlaneDistanceTo(plane, p1);
		const u32     g1 = IsGreaterThanOrEqualAll(d1, ScalarV(V_ZERO));

		if (dstCount < dstCountMax && g1 != g0) { dst[dstCount++] = (p0*d1 - p1*d0)/(d1 - d0); }
		if (dstCount < dstCountMax && g1      ) { dst[dstCount++] = p1; }

		p0 = p1;
		d0 = d1;
		g0 = g1;
	}

	return dstCount;
}

#endif // _COMMON_VECTOR_H_
