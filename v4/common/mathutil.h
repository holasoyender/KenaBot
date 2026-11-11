// ==========
// mathutil.h
// ==========

#ifndef _COMMON_MATHUTIL_H_
#define _COMMON_MATHUTIL_H_

#include "common.h"
#include "vector.h"

inline Vec3 Transform(const Vec3& transform_col0, const Vec3& transform_col1, const Vec3& transform_col2, const Vec3& transform_col3, const Vec3& p)
{
	const float x = transform_col0.x*p.x + transform_col1.x*p.y + transform_col2.x*p.z + transform_col3.x;
	const float y = transform_col0.y*p.x + transform_col1.y*p.y + transform_col2.y*p.z + transform_col3.y;
	const float z = transform_col0.z*p.x + transform_col1.z*p.y + transform_col2.z*p.z + transform_col3.z;

	return Vec3(x, y, z);
}

inline Vec3 Normalise(const Vec3& v)
{
	const float w = 1.0f/sqrtf(v.x*v.x + v.y*v.y + v.z*v.z);
	return Vec3(v.x*w, v.y*w, v.z*w);
}

inline Vec3 Cross(const Vec3& a, const Vec3& b)
{
	return Vec3(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
}

class Box2
{
public:
	Box2() {}
	Box2(const Vec4& v) : m_xmin(v.x), m_ymin(v.y), m_xmax(v.z), m_ymax(v.w) {}
	Box2(float xmin, float ymin, float xmax, float ymax) : m_xmin(xmin), m_ymin(ymin), m_xmax(xmax), m_ymax(ymax) {}

	static Box2 Invalid() { return Box2(+FLT_MAX, +FLT_MAX, -FLT_MAX, -FLT_MAX); }

	float GetCentreX() const { return (m_xmax + m_xmin)*0.5f; }
	float GetCentreY() const { return (m_ymax + m_ymin)*0.5f; }
	float GetExtentX() const { return (m_xmax - m_xmin)*0.5f; }
	float GetExtentY() const { return (m_ymax - m_ymin)*0.5f; }

	float m_xmin;
	float m_ymin;
	float m_xmax;
	float m_ymax;
};

float DistanceToBox(float x, float y, float xmin, float ymin, float xmax, float ymax);
float DistanceToSegment(float x, float y, float x0, float y0, float x1, float y1, float* t = NULL);

void MaurerEDT(int* img, int w, int h);

// http://paulbourke.net/miscellaneous/interpolation/
class BestFitLine
{
public:
	BestFitLine()
	{
		memset(this, 0, sizeof(*this));
	}

	void AddPoint(float x, float y)
	{
		m_sum_x  += x;
		m_sum_y  += y;
		m_sum_xx += x*x;
		m_sum_yy += y*y;
		m_sum_xy += x*y;
		m_sum_1  += 1.0f;
	}

	float GetLine(float& a, float& b) const
	{
		a = 0.0f;
		b = 0.0f;

		if (m_sum_1 >= 2.0f)
		{
			const float q = 1.0f/m_sum_1;

			const float sxx = m_sum_xx - m_sum_x*m_sum_x*q;
			const float syy = m_sum_yy - m_sum_y*m_sum_y*q;
			const float sxy = m_sum_xy - m_sum_x*m_sum_y*q;

			if (Abs<float>(sxx) != 0.0f)
			{
				b = sxy/sxx;
				a = (m_sum_y - b*m_sum_x)*q;

				if (Abs<float>(syy) != 0.0f)
				{
					return 1.0f;
				}
				else
				{
					return sxy/sqrtf(sxx*syy);
				}
			}
		}

		return 0.0f;
	}

private:
	float m_sum_x;
	float m_sum_y;
	float m_sum_xx;
	float m_sum_yy;
	float m_sum_xy;
	float m_sum_1;
};

#endif // _COMMON_MATHUTIL_H_
