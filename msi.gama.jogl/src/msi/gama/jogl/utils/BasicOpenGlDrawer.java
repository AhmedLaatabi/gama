package msi.gama.jogl.utils;

import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;

public class BasicOpenGlDrawer {

	// OpenGL member
	// private final GL gl;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer renderer;

	public JTSDrawer myJTSDrawer;

	public BasicOpenGlDrawer(final JOGLAWTGLRenderer gLRender) {

		// gl = gLRender.gl;
		renderer = gLRender;

		myJTSDrawer = new JTSDrawer(renderer);

	}

	/**
	 * Draw a geometry
	 * 
	 * @param geometry
	 */
	// public void drawJTSGeometry(GeometryObject geometry) {
	// if ( geometry.offSet.x != 0 || geometry.offSet.y != 0 ) {
	// gl.glTranslated(geometry.offSet.x, -geometry.offSet.y, 0.0f);
	// }
	// // TODO Scale en Z
	// gl.glScaled(geometry.scale.x, geometry.scale.y, 1);
	//
	// // Rotate angle (in XY plan)
	// if ( geometry.angle != 0 ) {
	// gl.glTranslatef((float) geometry.geometry.getCentroid().getX(), this.myJTSDrawer.yFlag *
	// (float) geometry.geometry.getCentroid().getY(), 0.0f);
	// gl.glRotatef(-geometry.angle, 0.0f, 0.0f, 1.0f);
	// gl.glTranslatef(-(float) geometry.geometry.getCentroid().getX(), -this.myJTSDrawer.yFlag *
	// (float) geometry.geometry.getCentroid().getY(), 0.0f);
	//
	// }
	//
	// for ( int i = 0; i < geometry.geometry.getNumGeometries(); i++ ) {
	// if ( geometry.geometry.getGeometryType() == "MultiPolygon" ) {
	// myJTSDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry, geometry.z_layer, geometry.color,
	// geometry.alpha, geometry.fill, geometry.border, geometry.angle, geometry.height, geometry.rounded);
	// }
	//
	// else if ( geometry.geometry.getGeometryType() == "Polygon" ) {
	// // The JTS geometry of a sphere is a circle (a polygon)
	// if ( geometry.type.equals("sphere") ) {
	// myJTSDrawer.DrawSphere(geometry.agent.getLocation(), geometry.z_layer, geometry.height,
	// geometry.color, geometry.alpha);
	// } else {
	// if ( geometry.height > 0 ) {
	// myJTSDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.z_layer, geometry.color,
	// geometry.alpha, geometry.fill, geometry.height, geometry.angle, true, geometry.border,
	// geometry.rounded);
	// } else {
	// myJTSDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.z_layer, geometry.color,
	// geometry.alpha, geometry.fill, geometry.border, geometry.isTextured, geometry.angle, true,
	// geometry.rounded);
	// }
	// }
	// } else if ( geometry.geometry.getGeometryType() == "MultiLineString" ) {
	//
	// myJTSDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, geometry.z_layer, geometry.color,
	// geometry.alpha, geometry.height);
	// }
	//
	// else if ( geometry.geometry.getGeometryType() == "LineString" ) {
	//
	// if ( geometry.height > 0 ) {
	// myJTSDrawer.DrawPlan((LineString) geometry.geometry, geometry.z_layer, geometry.color,
	// geometry.alpha, geometry.height, 0, true);
	// } else {
	// myJTSDrawer.DrawLineString((LineString) geometry.geometry, geometry.z_layer, 1.2f, geometry.color,
	// geometry.alpha);
	// }
	// }
	//
	// else if ( geometry.geometry.getGeometryType() == "Point" ) {
	// if ( geometry.height > 0 ) {
	// myJTSDrawer.DrawSphere(geometry.agent.getLocation(), geometry.z_layer, geometry.height,
	// geometry.color, geometry.alpha);
	// } else {
	// myJTSDrawer.DrawPoint((Point) geometry.geometry, geometry.z_layer, 10,
	// renderer.getMaxEnvDim() / 1000, geometry.color, geometry.alpha);
	// }
	// }
	// }
	// // Rotate angle (in XY plan)
	// if ( geometry.angle != 0 ) {
	// gl.glTranslatef((float) geometry.geometry.getCentroid().getX(), this.myJTSDrawer.yFlag *
	// (float) geometry.geometry.getCentroid().getY(), 0.0f);
	// gl.glRotatef(geometry.angle, 0.0f, 0.0f, 1.0f);
	// gl.glTranslatef(-(float) geometry.geometry.getCentroid().getX(), -this.myJTSDrawer.yFlag *
	// (float) geometry.geometry.getCentroid().getY(), 0.0f);
	//
	// }
	// gl.glScaled(1 / geometry.scale.x, 1 / geometry.scale.y, 1);
	// if ( geometry.offSet.x != 0 || geometry.offSet.y != 0 ) {
	// gl.glTranslated(-geometry.offSet.x, geometry.offSet.y, 0.0f);
	// }
	// }
	//
	// public void draw(CollectionObject collection) {
	// // Draw Shape file so need to inverse the y composante.
	// myJTSDrawer.yFlag = 1;
	// gl.glTranslated(-collection.collection.getBounds().centre().x, -collection.collection.getBounds().centre().y,
	// 0.0f);
	// // Iterate throught all the collection
	// SimpleFeatureIterator iterator = collection.collection.features();
	// // Color color= Color.red;
	// while (iterator.hasNext()) {
	// SimpleFeature feature = iterator.next();
	// Geometry sourceGeometry = (Geometry) feature.getDefaultGeometry();
	// if ( sourceGeometry.getGeometryType() == "MultiPolygon" ) {
	// myJTSDrawer.DrawMultiPolygon((MultiPolygon) sourceGeometry, 0.0f, collection.color, 1.0f, true, null,
	// 0, 0.0f, false);
	// } else if ( sourceGeometry.getGeometryType() == "Polygon" ) {
	// myJTSDrawer.DrawPolygon((Polygon) sourceGeometry, 0.0f, collection.color, 1.0f, true, null, false, 0,
	// true, false);
	// } else if ( sourceGeometry.getGeometryType() == "MultiLineString" ) {
	// myJTSDrawer.DrawMultiLineString((MultiLineString) sourceGeometry, 0.0f, collection.color, 1.0f, 0.0f);
	// } else if ( sourceGeometry.getGeometryType() == "LineString" ) {
	// myJTSDrawer.DrawLineString((LineString) sourceGeometry, 0.0f, 1.0f, collection.color, 1.0f);
	// } else if ( sourceGeometry.getGeometryType() == "Point" ) {
	// myJTSDrawer.DrawPoint((Point) sourceGeometry, 0.0f, 10, 10, collection.color, 1.0f);
	// }
	// }
	// gl.glTranslated(collection.collection.getBounds().centre().x, +collection.collection.getBounds().centre().y,
	// 0.0f);
	// myJTSDrawer.yFlag = -1;
	// }

}
