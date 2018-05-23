package mil.nga.geopackage.test.extension.related_tables;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import junit.framework.TestCase;
import mil.nga.geopackage.extension.RelatedTablesExtension;
import mil.nga.geopackage.extension.related_tables.ExtendedRelation;
import mil.nga.geopackage.extension.related_tables.UserMappingRow;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.test.LoadGeoPackageTestCase;
import mil.nga.geopackage.test.TestConstants;

/**
 * Test Related Tables Extension writing
 * 
 * @author jyutzler
 */
public class RelatedTablesWriteTest extends LoadGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public RelatedTablesWriteTest() {
		super(TestConstants.IMPORT_DB_FILE_NAME);
	}

	/**
	 * Test get tile
	 *
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test
	public void testGetRelationships() throws SQLException, IOException {

		RelatedTablesExtension rte = new RelatedTablesExtension(geoPackage);

		// 1. Has extension
		TestCase.assertFalse(rte.has());
		
		// 2. Add extension
		rte.getOrCreate();
		TestCase.assertTrue(rte.has());

		// 4. Get relationships
		Collection<ExtendedRelation> extendedRelations = rte.getRelationships();
		TestCase.assertEquals(0, extendedRelations.size());
		
		// 5. Add relationship between "geometry2d" and "geometry3d"
		final String baseTableName = "geometry2d";
		final String relatedTableName = "geometry3d";
		final String mappingTableName = "g2d_3d";
		final String relationshipName = "ggggg";
		ExtendedRelation extendedRelation = rte.addRelationship(baseTableName, relatedTableName, mappingTableName, relationshipName);
		extendedRelations = rte.getRelationships();
		TestCase.assertEquals(1, extendedRelations.size());
		TestCase.assertTrue(geoPackage.getDatabase().tableExists(mappingTableName));

		// 7. Add mappings
		FeatureDao baseDao = geoPackage.getFeatureDao(baseTableName);
		FeatureDao relatedDao = geoPackage.getFeatureDao(relatedTableName);
		FeatureResultSet baseFrs = baseDao.queryForAll();
		int baseCount = baseFrs.getCount();
		long[] baseIds = new long[baseCount]; 
		int inx=0;
		while(baseFrs.moveToNext()) {
			baseIds[inx++] = baseFrs.getRow().getId();
		}
		FeatureResultSet relatedFrs = relatedDao.queryForAll();
		int relatedCount = relatedFrs.getCount();
		long[] relatedIds = new long[relatedCount]; 
		inx=0;
		while(relatedFrs.moveToNext()) {
			relatedIds[inx++] = relatedFrs.getRow().getId();
		}
		UserMappingRow umr = null;
		for (inx = 0; inx < 10; inx++){
			umr = new UserMappingRow(
					((int)Math.floor(Math.random() * baseCount)),
					((int)Math.floor(Math.random() * relatedCount)));
			rte.addMapping(extendedRelation, umr);// How do we test that this worked?
		}

		// 8. Remove mappings (note: it is plausible and allowed 
		// to have duplicate entries)
		TestCase.assertTrue(rte.removeMapping(extendedRelation, umr) > 0);

		// 6. Remove relationship
		rte.removeRelationship(baseTableName, relatedTableName, relationshipName);
		extendedRelations = rte.getRelationships();
		TestCase.assertEquals(0, extendedRelations.size());
		TestCase.assertFalse(geoPackage.getDatabase().tableExists(mappingTableName));

		// 3. Remove extension
		rte.removeExtension();
		TestCase.assertFalse(rte.has());
	}
}