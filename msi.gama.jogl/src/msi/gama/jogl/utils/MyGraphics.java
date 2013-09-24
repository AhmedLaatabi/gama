package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.TessellCallBack;
import msi.gama.metamodel.shape.GamaPoint;
import com.sun.opengl.util.GLUT;
import com.vividsolutions.jts.geom.*;

public class MyGraphics {

	// OpenGL member
	private final GL gl;
	private final GLU myGlu;
	private final TessellCallBack tessCallback;
	private final GLUtessellator tobj;
	// Use to draw Bitmap String
	// private final GLUT glut;
	// public BasicOpenGlDrawer basicDrawer;
	// public DisplayListHandler displayListHandler;
	public VertexArrayHandler vertexArrayHandler;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	double alpha = 1.0d;

	public MyGraphics(final JOGLAWTGLRenderer myGLRender) {

		gl = myGLRender.gl;
		myGlu = myGLRender.glu;
		// basicDrawer = new BasicOpenGlDrawer(myGLRender);
		// displayListHandler = new DisplayListHandler(myGl, myGlu, myGLRender);
		vertexArrayHandler = new VertexArrayHandler(gl, myGlu, myGLRender);
		tessCallback = new TessellCallBack(gl, myGlu);
		tobj = myGlu.gluNewTess();

		// glut = new GLUT();

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

	}

	void DrawTorus(final double innerRadius, final double outterRadius, final int sides, final int rings) {
		GLUT glut = new GLUT();
		glut.glutSolidTorus(innerRadius, outterRadius, sides, rings);
	}

	public void DrawArcBall(final int size, final GamaPoint pos) {

		gl.glTranslated(pos.x, -pos.y, pos.z);
		// DrawXYZAxis(1.0d);
		double innerRadius = 0.02d * size;
		double outterRadius = 1.0d * size;
		gl.glColor3d(0.0d, 0.0d, 1.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);

		gl.glRotated(90, 0.0d, 1.0d, 0.0d);
		gl.glColor3d(1.0d, 0.0d, 0.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);
		gl.glRotated(90, 0.0d, -1.0d, 0.0d);

		gl.glRotated(90, 1.0d, 0.0d, 0.0d);
		gl.glColor3d(0.0d, 1.0d, 0.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);
		gl.glTranslated(-pos.x, pos.y, -pos.z);
	}

	// ////////////////////////////////////// Rectangle with curved corner ////////////////////////////

	public void DrawRoundRectangle(final Polygon p) {

		double width = p.getEnvelopeInternal().getWidth();
		double height = p.getEnvelopeInternal().getHeight();

		gl.glTranslated(p.getCentroid().getX(), -p.getCentroid().getY(), 0.0d);
		DrawRectangle(width, height * 0.8, p.getCentroid());
		DrawRectangle(width * 0.8, height, p.getCentroid());
		DrawRoundCorner(width, height, width * 0.1, height * 0.1, 5);
		gl.glTranslated(-p.getCentroid().getX(), p.getCentroid().getY(), 0.0d);

	}

	void DrawRectangle(final double width, final double height, final Point point) {
		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3d(-width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, -height / 2, 0.0d);
		gl.glVertex3d(-width / 2, -height / 2, 0.0d);
		gl.glEnd();
	}

	void DrawFan(final double radius, final double x, final double y, final int or_x, final int or_y, final int timestep) {
		gl.glBegin(GL_TRIANGLE_FAN); // upper right
		gl.glVertex3d(or_x * x, or_y * y, 0.0d);
		for ( int i = 0; i <= timestep; i++ ) {
			double anglerad = Math.PI / 2 * i / timestep;
			double xi = Math.cos(anglerad) * radius;
			double yi = Math.sin(anglerad) * radius;
			gl.glVertex3d(or_x * (x + xi), y + yi, 0.0d);
		}
		gl.glEnd();
	}

	void DrawRoundCorner(final double width, final double height, final double x_radius, final double y_radius,
		final int nbPoints) {

		double xc = width / 2 * 0.8;
		double yc = height / 2 * 0.8;
		// Enhancement implement DrawFan(radius, xc, yc, 10);

		gl.glBegin(GL_TRIANGLE_FAN); // upper right
		gl.glVertex3d(xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN); // upper right

		gl.glVertex3d(xc, -yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, -(yc + yi), 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN); // upper left

		gl.glVertex3d(-xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN);
		gl.glVertex3d(-xc, -yc, 0.0d); // down left
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), -(yc + yi), 0.0d);
		}
		gl.glEnd();
	}

}
