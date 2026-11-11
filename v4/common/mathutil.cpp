// ============
// mathutil.cpp
// ============

#include "mathutil.h"

float DistanceToBox(float x, float y, float xmin, float ymin, float xmax, float ymax)
{
	const float dx = Max<float>(0.0f, Abs<float>(x - (xmax + xmin)*0.5f) - (xmax - xmin)*0.5f);
	const float dy = Max<float>(0.0f, Abs<float>(y - (ymax + ymin)*0.5f) - (ymax - ymin)*0.5f);

	return sqrtf(dx*dx + dy*dy);
}

float DistanceToSegment(float x, float y, float x0, float y0, float x1, float y1, float* t)
{
	float nx = x1 - x0;
	float ny = y1 - y0;
	float q = nx*nx + ny*ny;
	float dx = 0.0f;
	float dy = 0.0f;
	float dz = 0.0f;

	if (q < 0.00001f)
	{
		dx = x - (x1 + x0)*0.5f;
		dy = y - (y1 + y0)*0.5f;

		if (t) { *t = 0.5f; }
	}
	else
	{
		const float len = sqrtf(q);

		dz = ((x - x0)*nx + (y - y0)*ny)/len; // projected distance along segment

		if (dz >= len)
		{
			dx = x - x1;
			dy = y - y1;
			dz = 0.0f;

			if (t) { *t = 1.0f; }
		}
		else
		{
			dx = x - x0;
			dy = y - y0;

			if (dz <= 0.0f)
			{
				dz = 0.0f;

				if (t) { *t = 0.0f; }
			}
			else
			{
				if (t) { *t = dz/len; }
			}
		}
	}

	return sqrtf(Max<float>(0.0f, dx*dx + dy*dy - dz*dz));
}

namespace EDT {

// Euclidean Distance Transform (EDT)
// ----------------------------------------------------------------
// Algorithm based on Ricardo Fabbri's implementation of Maurer EDT
// http://www.lems.brown.edu/~rfabbri/stuff/fabbri-EDT-survey-ACMCSurvFeb2008.pdf
// http://tc18.liris.cnrs.fr/subfields/distance_skeletons/DistanceTransform.pdf
// http://www.lems.brown.edu/vision/people/leymarie/Refs/CompVision/DT/DTpaper.pdf
// http://www.comp.nus.edu.sg/~tants/jfa/i3d06.pdf (jump flooding)
// http://www.comp.nus.edu.sg/~tants/jfa/rong-guodong-phd-thesis.pdf
// http://en.wikipedia.org/wiki/Distance_transform

static __forceinline void MaurerEDT_Horizontal(int* img, int w, int /*h*/, int y)
{
	int* row = &img[y*w];

	if (row[0] != 0)
	{
		row[0] = w;
	}

	for (int x = 1; x < w; x++)
	{
		if (row[x] != 0)
		{
			row[x] = row[x - 1] + 1;
		}
	}

	for (int x = w - 2; x >= 0; x--)
	{
		if (row[x] > row[x + 1] + 1)
		{
			row[x] = row[x + 1] + 1;
		}
	}

	for (int x = 0; x < w; x++)
	{
		if (row[x] >= w)
		{
			row[x] = -1; // -1 is "infinity"
		}
		else
		{
			row[x] = row[x]*row[x]; // distance squared
		}
	}
}

static __forceinline bool MaurerEDT_Remove(int du, int dv, int dw, int u, int v, int w)
{
	s64 a = v - u; // these need to be 64-bit ints so the calculation below doesn't overflow
	s64 b = w - v;
	s64 c = w - u;

	return (c*dv - b*du - a*dw) > a*b*c;
}

static void MaurerEDT_Vertical(int* img, int w, int h, int x, int* G, int* H)
{
	int l1 = -1;
	int l2 = 0;

	int* col = img + x;

	for (int y = 0; y < h; y++)
	{
		const int fi = *col; col += w;

		if (fi != -1)
		{
			while (l1 > 0 && MaurerEDT_Remove(G[l1-1], G[l1], fi, H[l1-1], H[l1], y))
			{
				l1--;
			}

			l1++;
			G[l1] = fi;
			H[l1] = y;
		}
	}

	if (l1 == -1)
	{
		return;
	}

	col = img + x;

	for (int y = 0; y < h; y++)
	{
		int tmp0 = H[l2] - y;
		int tmp1 = G[l2] + tmp0*tmp0;

		while (l2 < l1)
		{
			const int tmp2 = H[l2+1] - y;

			if (tmp1 <= G[l2+1] + tmp2*tmp2)
			{
				break;
			}

			l2++;
			tmp0 = H[l2] - y;
			tmp1 = G[l2] + tmp0*tmp0;
		}

		*col = tmp1; col += w;
	}
}

} // namespace EDT

void MaurerEDT(int* img, int w, int h)
{
	int* G = new int[h];
	int* H = new int[h];

	for (int y = 0; y < h; y++)
	{
		EDT::MaurerEDT_Horizontal(img, w, h, y);
	}

	for (int x = 0; x < w; x++)
	{
		EDT::MaurerEDT_Vertical(img, w, h, x, G, H);
	}

	delete[] G;
	delete[] H;
}
